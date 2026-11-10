package com.ledgerflow.transaction;

import com.ledgerflow.account.Account;
import com.ledgerflow.account.AccountRepository;
import com.ledgerflow.transaction.dto.CreateTransferRequest;
import com.ledgerflow.transaction.dto.TransactionResponse;
import com.ledgerflow.user.User;
import com.ledgerflow.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponse createTransfer(
            String email,
            CreateTransferRequest request
    ) {
        // 1. Fetch the authenticated user from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        // 2. Validate the amount
        if (request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transfer amount must be greater than zero"
            );
        }

        // 3. Verify that the sender account belongs to this user.
        // This check must happen before returning an existing transaction.
        Account sender = accountRepository
                .findWithLockByAccountNumberAndUser(
                        request.senderAccountNumber(),
                        user
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sender account not found"
                ));

        // 4. Check whether this request was already processed
        var existingTransaction = transactionRepository
                .findByIdempotencyKey(request.idempotencyKey());

        if (existingTransaction.isPresent()) {
            Transaction existing = existingTransaction.get();

            boolean sameRequest =
                    existing.getSenderAccount().getAccountNumber()
                            .equals(request.senderAccountNumber())
                            && existing.getReceiverAccount().getAccountNumber()
                            .equals(request.receiverAccountNumber())
                            && existing.getAmount().compareTo(request.amount()) == 0
                            && Objects.equals(
                            existing.getDescription(),
                            request.description()
                    );

            if (!sameRequest) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key was already used for a different request"
                );
            }

            return toResponse(existing);
        }

        // 5. Fetch and lock the receiver account
        Account receiver = accountRepository
                .findWithLockByAccountNumber(
                        request.receiverAccountNumber()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Receiver account not found"
                ));

        // 6. Validate the accounts
        if (sender.getAccountNumber().equals(receiver.getAccountNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sender and receiver accounts must be different"
            );
        }

        if (!"ACTIVE".equals(sender.getStatus())
                || !"ACTIVE".equals(receiver.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Both accounts must be active"
            );
        }

        if (sender.getBalance().compareTo(request.amount()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient balance"
            );
        }

        // 7. Update balances inside the same database transaction
        sender.setBalance(
                sender.getBalance().subtract(request.amount())
        );

        receiver.setBalance(
                receiver.getBalance().add(request.amount())
        );

        // 8. Create the transaction record
        Transaction transaction = new Transaction();
        transaction.setReferenceNumber(generateReferenceNumber());
        transaction.setIdempotencyKey(request.idempotencyKey());
        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);
        transaction.setAmount(request.amount());
        transaction.setStatus("COMPLETED");
        transaction.setDescription(request.description());

        // 9. Save and return the transaction
        Transaction saved = transactionRepository.save(transaction);

        return toResponse(saved);
    }

    private String generateReferenceNumber() {
        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toUpperCase();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getReferenceNumber(),
                transaction.getSenderAccount().getAccountNumber(),
                transaction.getReceiverAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }

    @Transactional
    public Page<TransactionResponse> getTransactionHistory(
            String email,
            int page,
            int size,
            LocalDate fromDate,
            LocalDate toDate,
            String status,
            String transactionType) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        // Validate date range
        if ((fromDate == null) != (toDate == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Both fromDate and toDate must be provided"
            );
        }

        if (fromDate != null && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fromDate cannot be after toDate"
            );
        }

        // Validate status
        if (status != null) {
            status = status.toUpperCase(Locale.ROOT);

            if (!List.of("COMPLETED", "PENDING", "FAILED").contains(status)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid transaction status"
                );
            }
        }

        // Validate transaction type
        if (transactionType == null) {
            transactionType = "ALL";
        } else {
            transactionType = transactionType.toUpperCase(Locale.ROOT);
        }

        if (!List.of("SENT", "RECEIVED", "ALL").contains(transactionType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid transaction type"
            );
        }

        // Convert dates into Instants
        Instant startDate = null;
        Instant endDate = null;

        if (fromDate != null) {
            ZoneId zone = ZoneId.of("Asia/Kolkata");

            startDate = fromDate.atStartOfDay(zone).toInstant();

            endDate = toDate.plusDays(1)
                    .atStartOfDay(zone)
                    .toInstant();
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactions =
                transactionRepository.findTransactionsWithFilters(
                        user,
                        transactionType,
                        status,
                        startDate,
                        endDate,
                        pageable
                );

        return transactions.map(this::toResponse);
    }

    @Transactional
    public TransactionResponse getTransactionByReference(
            String email,
            String referenceNumber) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        Transaction transaction = transactionRepository
                .findByReferenceNumberAndUser(referenceNumber, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found"
                ));

        return toResponse(transaction);
    }
}

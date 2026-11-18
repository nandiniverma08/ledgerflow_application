package com.ledgerflow.ledger;

import com.ledgerflow.account.Account;
import com.ledgerflow.account.AccountRepository;
import com.ledgerflow.ledger.dto.AccountLedgerSummaryResponse;
import com.ledgerflow.ledger.dto.LedgerEntryResponse;
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


@Service
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public LedgerService(LedgerEntryRepository ledgerEntryRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Page<LedgerEntryResponse> getLedgerEntriesByAccount(
            String email,
            String accountNumber,
            int page,
            int size,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        // 1. Find authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        // 2. Find account and verify ownership
        Account account = accountRepository
                .findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        // 3. Validate dates
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

// 4. Create pagination request
        Pageable pageable = PageRequest.of(page, size);

// 5. Fetch account ledger entries
        Page<LedgerEntry> entries;

        if (fromDate == null) {
            // No date filter: fetch all entries
            entries = ledgerEntryRepository.findByAccountOrderByCreatedAtDesc(
                    account, pageable
            );
        } else {
            ZoneId zone = ZoneId.of("Asia/Kolkata");

            Instant startDate = fromDate.atStartOfDay(zone).toInstant();
            Instant endDate = toDate.plusDays(1).atStartOfDay(zone).toInstant();

            entries = ledgerEntryRepository.findAccountLedgerByDateRange(
                    account, startDate, endDate, pageable
            );
        }

        // 5. Convert Entity to DTO
        return entries.map(ledgerEntry -> new LedgerEntryResponse(
                ledgerEntry.getAccount().getAccountNumber(),
                ledgerEntry.getEntryType().name(),
                ledgerEntry.getAmount(),
                ledgerEntry.getCreatedAt()
        ));
    }


    @Transactional
    public AccountLedgerSummaryResponse fetchAccountLedgerSummary(
            String email,
            String accountNumber
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        Account account = accountRepository
                .findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        List<Object[]> results =
                ledgerEntryRepository.calculateAccountLedgerSummary(account);

        Object[] result = results.get(0);

        BigDecimal totalCredits = result[0] != null
                ? (BigDecimal) result[0]
                : BigDecimal.ZERO;

        BigDecimal totalDebits = result[1] != null
                ? (BigDecimal) result[1]
                : BigDecimal.ZERO;

        BigDecimal netMovement = totalCredits.subtract(totalDebits);

        return new AccountLedgerSummaryResponse(
                account.getAccountNumber(),
                totalCredits,
                totalDebits,
                netMovement
        );
    }

}

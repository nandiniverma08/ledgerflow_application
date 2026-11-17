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
            int size
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

        // 3. Create pagination request
        Pageable pageable = PageRequest.of(page, size);

        // 4. Fetch account ledger entries
        Page<LedgerEntry> entries =
                ledgerEntryRepository.findByAccountOrderByCreatedAtDesc(
                        account, pageable
                );

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

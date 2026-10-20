package com.ledgerflow.account;

import com.ledgerflow.account.dto.AccountResponse;
import com.ledgerflow.user.User;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse createAccount(User user) {
        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getStatus()
        );
    }

    private String generateAccountNumber() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }

    public List<AccountResponse> getMyAccounts(User user) {
        return accountRepository.findByUser(user)
                .stream()
                .map(account -> new AccountResponse(
                        account.getAccountNumber(),
                        account.getBalance(),
                        account.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountByNumber(
            String accountNumber,
            User user
    ) {
        Account account = accountRepository
                .findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        return new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus()
        );
    }
}

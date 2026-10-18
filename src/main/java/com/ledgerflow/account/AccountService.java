package com.ledgerflow.account;

import com.ledgerflow.account.dto.AccountResponse;
import com.ledgerflow.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

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
}

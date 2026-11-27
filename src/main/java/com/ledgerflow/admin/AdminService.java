package com.ledgerflow.admin;

import com.ledgerflow.account.Account;
import com.ledgerflow.account.AccountRepository;
import com.ledgerflow.admin.dto.AdminAccountResponse;
import com.ledgerflow.admin.dto.AdminUserResponse;
import com.ledgerflow.admin.dto.FreezeAccountResponse;
import com.ledgerflow.user.User;
import com.ledgerflow.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AdminService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getStatus(),
                        user.getRole().name(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminAccountResponse> getAllAccounts() {

        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> new AdminAccountResponse(
                        account.getAccountNumber(),
                        account.getUser().getFirstName() + " " +
                                account.getUser().getLastName(),
                        account.getUser().getEmail(),
                        account.getBalance(),
                        account.getStatus(),
                        account.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public FreezeAccountResponse freezeByAccountNumber(
            String accountNumber,
            String reason,
            String adminEmail) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account does not exist"));

        if ("FROZEN".equals(account.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Account is already frozen");
        }

        account.setStatus("FROZEN");

        accountRepository.save(account);

        Instant frozenAt = Instant.now();

        return new FreezeAccountResponse(
                account.getAccountNumber(),
                account.getUser().getFirstName() + " " +
                        account.getUser().getLastName(),
                account.getStatus(),
                reason,
                frozenAt
        );
    }
}

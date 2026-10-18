package com.ledgerflow.account;

import com.ledgerflow.account.dto.AccountResponse;
import com.ledgerflow.user.User;
import com.ledgerflow.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    public AccountController(
            AccountService accountService,
            UserRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return accountService.createAccount(user);
    }
}

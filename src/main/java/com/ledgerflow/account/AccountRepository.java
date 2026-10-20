package com.ledgerflow.account;

import com.ledgerflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser(User user);


    Optional<Account> findByAccountNumberAndUser(
            String accountNumber,
            User user
    );

}

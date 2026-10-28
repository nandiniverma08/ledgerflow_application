package com.ledgerflow.account;

import com.ledgerflow.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser(User user);


    Optional<Account> findByAccountNumberAndUser(
            String accountNumber,
            User user
    );

    Optional<Account> findByAccountNumber(String accountNumber);

    // Lock sender account while processing a transfer
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findWithLockByAccountNumberAndUser(
            String accountNumber,
            User user
    );

    // Lock receiver account while processing a transfer
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findWithLockByAccountNumber(
            String accountNumber
    );

}

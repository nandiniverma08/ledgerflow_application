package com.ledgerflow.transaction;

import com.ledgerflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByReferenceNumber(String referenceNumber);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction>
    findBySenderAccount_UserOrReceiverAccount_UserOrderByCreatedAtDesc(
            User senderUser,
            User receiverUser
    );

}

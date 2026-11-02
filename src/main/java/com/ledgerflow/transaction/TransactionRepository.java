package com.ledgerflow.transaction;

import com.ledgerflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(value = """
    SELECT t.*
    FROM transactions t
    JOIN accounts s ON t.sender_account_id = s.id
    JOIN accounts r ON t.receiver_account_id = r.id
    WHERE t.reference_number = :referenceNumber
    AND (
        s.user_id = :userId
        OR r.user_id = :userId
    )
    """, nativeQuery = true)
    Optional<Transaction> findByReferenceNumberAndUser(
            @Param("referenceNumber") String referenceNumber,
            @Param("userId") Long userId
    );

}

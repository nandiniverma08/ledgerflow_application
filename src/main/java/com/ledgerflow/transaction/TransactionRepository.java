package com.ledgerflow.transaction;

import com.ledgerflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByReferenceNumber(String referenceNumber);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    Page<Transaction> findBySenderAccount_UserOrReceiverAccount_UserOrderByCreatedAtDesc(
            User senderUser,
            User receiverUser,
            Pageable pageable
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

    @Query("""
    SELECT t FROM Transaction t
    WHERE (t.senderAccount.user = :user OR t.receiverAccount.user = :user)
    AND t.createdAt >= :startDate
    AND t.createdAt < :endDate
    ORDER BY t.createdAt DESC
""")
    Page<Transaction> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE
        (
            (:transactionType = 'SENT' AND t.senderAccount.user = :user)
            OR
            (:transactionType = 'RECEIVED' AND t.receiverAccount.user = :user)
            OR
            (:transactionType = 'ALL' AND
                (t.senderAccount.user = :user OR t.receiverAccount.user = :user))
        )
        AND (:status IS NULL OR t.status = :status)
        AND t.createdAt >= :startDate
        AND t.createdAt < :endDate
    ORDER BY t.createdAt DESC
""")
    Page<Transaction> findTransactionsWithFilters(
            @Param("user") User user,
            @Param("transactionType") String transactionType,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

}

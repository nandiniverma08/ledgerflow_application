package com.ledgerflow.ledger;

import com.ledgerflow.account.Account;
import com.ledgerflow.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByTransactionOrderByCreatedAtAsc(
            Transaction transaction
    );

    Page<LedgerEntry> findByAccountOrderByCreatedAtDesc(
            Account account,
            Pageable pageable
    );

    @Query("""
    SELECT
        SUM(CASE WHEN le.entryType = 'CREDIT' THEN le.amount ELSE 0 END),
        SUM(CASE WHEN le.entryType = 'DEBIT' THEN le.amount ELSE 0 END)
    FROM LedgerEntry le
    WHERE le.account = :account
""")
    List<Object[]> calculateAccountLedgerSummary(@Param("account") Account account);
}

package com.ledgerflow.ledger;

import com.ledgerflow.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByTransactionOrderByCreatedAtAsc(
            Transaction transaction
    );
}

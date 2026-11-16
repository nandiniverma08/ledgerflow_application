package com.ledgerflow.ledger;

import com.ledgerflow.account.Account;
import com.ledgerflow.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByTransactionOrderByCreatedAtAsc(
            Transaction transaction
    );

    Page<LedgerEntry> findByAccountOrderByCreatedAtDesc(
            Account account,
            Pageable pageable
    );
}

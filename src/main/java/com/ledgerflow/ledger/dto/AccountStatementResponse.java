package com.ledgerflow.ledger.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AccountStatementResponse(
        String accountNumber,
        List<StatementEntry> statement
) {

    public record StatementEntry(
            Instant date,
            String referenceNumber,
            String description,
            BigDecimal debit,
            BigDecimal credit,
            BigDecimal balance
    ) {}
}
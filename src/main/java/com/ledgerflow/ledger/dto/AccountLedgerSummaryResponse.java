package com.ledgerflow.ledger.dto;

import java.math.BigDecimal;

public record AccountLedgerSummaryResponse(
        String accountNumber,
        BigDecimal totalCredits,
        BigDecimal totalDebits,
        BigDecimal netMovement
) {}

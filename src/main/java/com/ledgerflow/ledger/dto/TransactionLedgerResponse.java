package com.ledgerflow.ledger.dto;

import java.math.BigDecimal;
import java.util.List;

public record TransactionLedgerResponse(
        String referenceNumber,
        BigDecimal amount,
        List<LedgerEntryResponse> entries
) {}
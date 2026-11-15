package com.ledgerflow.ledger.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record LedgerEntryResponse(
        String accountNumber,
        String entryType,
        BigDecimal amount,
        Instant createdAt
) {}
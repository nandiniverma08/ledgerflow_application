package com.ledgerflow.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        String referenceNumber,
        String senderAccountNumber,
        String receiverAccountNumber,
        BigDecimal amount,
        String status,
        String description,
        Instant createdAt
) {}

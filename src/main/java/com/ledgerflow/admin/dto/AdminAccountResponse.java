package com.ledgerflow.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminAccountResponse(
        String accountNumber,
        String customerName,
        String customerEmail,
        BigDecimal balance,
        String status,
        Instant createdAt
) {}

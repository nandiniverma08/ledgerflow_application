package com.ledgerflow.admin.dto;

import java.time.Instant;

public record FreezeAccountResponse(
        String accountNumber,
        String customerName,
        String status,
        String reason,
        Instant frozenAt
) {}

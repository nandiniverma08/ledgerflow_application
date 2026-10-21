package com.ledgerflow.beneficiary.dto;

import java.time.Instant;

public record BeneficiaryResponse(
        Long id,
        String beneficiaryName,
        String accountNumber,
        String bankName,
        String status,
        Instant createdAt
) {}

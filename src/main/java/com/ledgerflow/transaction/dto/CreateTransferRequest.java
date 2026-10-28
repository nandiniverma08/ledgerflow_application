package com.ledgerflow.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTransferRequest(
        @NotBlank
        @Size(max = 30)
        String senderAccountNumber,

        @NotBlank
        @Size(max = 30)
        String receiverAccountNumber,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @Size(max = 255)
        String description,

        @NotBlank
        @Size(max = 100)
        String idempotencyKey
) {
}

package com.ledgerflow.account.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        BigDecimal balance,
        String status
) {
}

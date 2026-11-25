package com.ledgerflow.admin.dto;

import java.time.Instant;

public record AdminUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String status,
        String role,
        Instant createdAt
) {
}

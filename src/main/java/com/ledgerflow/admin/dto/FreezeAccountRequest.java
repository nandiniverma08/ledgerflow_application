package com.ledgerflow.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FreezeAccountRequest(
        @NotBlank(message = "Reason is required")
        @Size(max = 255)
        String reason
) {}

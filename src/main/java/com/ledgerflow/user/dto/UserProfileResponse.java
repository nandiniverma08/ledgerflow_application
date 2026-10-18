package com.ledgerflow.user.dto;

public record UserProfileResponse(
        String firstName,
        String lastName,
        String email,
        String phone,
        String status
){
}

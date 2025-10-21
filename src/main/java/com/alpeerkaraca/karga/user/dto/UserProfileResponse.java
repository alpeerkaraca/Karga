package com.alpeerkaraca.karga.user.dto;

public record UserProfileResponse(
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}

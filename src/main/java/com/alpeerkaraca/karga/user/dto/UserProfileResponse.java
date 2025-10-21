package com.alpeerkaraca.karga.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserProfileResponse(
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}

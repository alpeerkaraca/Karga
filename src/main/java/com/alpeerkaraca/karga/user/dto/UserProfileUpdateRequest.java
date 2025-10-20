package com.alpeerkaraca.karga.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserProfileUpdateRequest(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String phoneNumber
) {
}

package com.alpeerkaraca.karga.DTO;

import jakarta.validation.constraints.NotBlank;

public record UserProfileResponse(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String phoneNumber,
        @NotBlank
        String email
) {
}

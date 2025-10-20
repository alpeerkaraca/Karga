package com.alpeerkaraca.karga.DTO;

import jakarta.validation.constraints.Email;
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

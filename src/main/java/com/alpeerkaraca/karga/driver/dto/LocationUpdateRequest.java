package com.alpeerkaraca.karga.driver.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationUpdateRequest(
        @NotBlank
        double latitude,
        @NotBlank
        double longitude
) {
}

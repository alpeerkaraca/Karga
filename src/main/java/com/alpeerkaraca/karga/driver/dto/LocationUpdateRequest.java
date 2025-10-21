package com.alpeerkaraca.karga.driver.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record LocationUpdateRequest(
        @Min(-90) @Max(90)
        double latitude,
        @Min(-180) @Max(180)
        double longitude
) {
}

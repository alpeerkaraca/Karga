package com.alpeerkaraca.karga.trip.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CurrentLocation(
        @NotNull
        @Min(-90) @Max(90)
        double latitude,
        @NotNull
        @Min(-180) @Max(180)
        double longitude

) {
}

package com.alpeerkaraca.karga.DTO;

import com.alpeerkaraca.karga.Models.DriverStatus;

public record DriverUpdateStatus(
        DriverStatus status,
        double longitude,
        double latitude
) {
}

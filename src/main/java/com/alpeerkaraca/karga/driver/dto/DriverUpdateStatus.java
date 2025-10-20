package com.alpeerkaraca.karga.driver.dto;

import com.alpeerkaraca.karga.driver.domain.DriverStatus;

public record DriverUpdateStatus(
        DriverStatus status,
        double longitude,
        double latitude
) {
}

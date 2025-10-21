package com.alpeerkaraca.karga.trip.dto;

import java.util.UUID;

public record NearbyDriversResponse(
        UUID driverId,
        double latitude,
        double longitude,
        double distanceKm
) {
}

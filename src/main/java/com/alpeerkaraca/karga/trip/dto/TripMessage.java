package com.alpeerkaraca.karga.trip.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record TripMessage(
        String eventType,
        UUID tripId,
        UUID driverId,
        Timestamp timestamp
) {
}

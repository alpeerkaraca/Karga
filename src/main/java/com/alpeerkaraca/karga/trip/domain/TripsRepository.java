package com.alpeerkaraca.karga.trip.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripsRepository extends JpaRepository<Trips, UUID> {
    @Query("SELECT t FROM Trips t WHERE t.tripStatus = 'REQUESTED' AND t.driver IS NULL")
    List<Trips> findAvailableTrips();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trips t WHERE t.tripId = :tripId")
    Optional<Trips> findByIdForUpdate(UUID tripId);
}

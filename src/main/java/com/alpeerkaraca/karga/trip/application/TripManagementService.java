package com.alpeerkaraca.karga.trip.application;

import com.alpeerkaraca.karga.core.exception.ConflictException;
import com.alpeerkaraca.karga.core.exception.ResourceNotFoundException;
import com.alpeerkaraca.karga.driver.application.DriverService;
import com.alpeerkaraca.karga.driver.application.DriverStatusService;
import com.alpeerkaraca.karga.driver.domain.Driver;
import com.alpeerkaraca.karga.trip.domain.TripStatus;
import com.alpeerkaraca.karga.trip.domain.Trips;
import com.alpeerkaraca.karga.trip.domain.TripsRepository;
import com.alpeerkaraca.karga.trip.dto.CurrentLocation;
import com.alpeerkaraca.karga.trip.dto.TripMessage;
import com.alpeerkaraca.karga.user.application.UserService;
import com.alpeerkaraca.karga.user.domain.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripManagementService {
    private final TripsRepository tripsRepository;
    private final DriverStatusService driverStatusService;
    private final TripRequestService tripRequestService;
    private final DriverService driverService;
    private final KafkaTemplate<String, TripMessage> kafkaTemplate;
    private final String TOPIC_TRIP_EVENTS = "trip_events";
    private final UserService userService;

    public List<Trips> getAvailableTrips() {
        return tripsRepository.findAvailableTrips();
    }
    @Transactional
    public Trips acceptTrip(UUID tripId, UUID driverId) {
        Trips trip = tripsRepository.findByIdForUpdate(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Yolculuk bulunamadı: " + tripId));

        if (trip.getTripStatus() != TripStatus.REQUESTED) {
            throw new ConflictException("Yolculuk zaten kabul edilmiş.");
        }

        Users driver = userService.getUserById(driverId);
        trip.setDriver(driver);

        trip.setTripStatus(TripStatus.ACCEPTED);
        trip.setStartedAt(Timestamp.from(Instant.now()));
        Trips savedTrip = tripsRepository.save(trip);

        TripMessage kafkaMessage = new TripMessage(
                "TRIP_ACCEPTED",
                savedTrip.getTripId(),
                savedTrip.getDriver().getUserId(),
                Timestamp.valueOf(LocalDateTime.now())
        );
        kafkaTemplate.send(TOPIC_TRIP_EVENTS, kafkaMessage);

        return savedTrip;
    }

    public void startTrip(UUID tripId) {
        Trips trip = tripsRepository.findById(tripId).orElseThrow(() -> new ResourceNotFoundException("Yolculuk bulunamadı: " + tripId));
        if (trip.getTripStatus() != TripStatus.ACCEPTED) {
            throw new ConflictException("Yolculuk başlatılamaz. Yolculuk kabul edilmemiş.");
        } else {
            trip.setTripStatus(TripStatus.IN_PROGRESS);
            trip.setStartedAt(Timestamp.from(Instant.now()));
            tripsRepository.save(trip);
            TripMessage kafkaMessage = new TripMessage(
                    "TRIP_STARTED",
                    trip.getTripId(),
                    trip.getDriver().getUserId(),
                    Timestamp.valueOf(LocalDateTime.now())
            );
            kafkaTemplate.send(TOPIC_TRIP_EVENTS, kafkaMessage);
        }
    }

    public void completeTrip(UUID tripId) {
        Trips trip = tripsRepository.findById(tripId).orElseThrow(() -> new ResourceNotFoundException("Yolculuk bulunamadı: " + tripId));
        if (trip.getTripStatus() != TripStatus.IN_PROGRESS) {
            throw new ConflictException("Yolculuk tamamlanamaz. Yolculuk başlatılmamış.");
        } else {
            trip.setTripStatus(TripStatus.COMPLETED);
            trip.setEndedAt(Timestamp.from(Instant.now()));
            tripsRepository.save(trip);
            TripMessage kafkaMessage = new TripMessage(
                    "TRIP_COMPLETED",
                    trip.getTripId(),
                    trip.getDriver().getUserId(),
                    Timestamp.valueOf(LocalDateTime.now())
            );
            kafkaTemplate.send(TOPIC_TRIP_EVENTS, kafkaMessage);
        }

    }

    public void cancelTrip(UUID tripId) {
        Trips trip = tripsRepository.findById(tripId).orElseThrow(() -> new ResourceNotFoundException("Yolculuk bulunamadı: " + tripId));
        if (trip.getTripStatus() == TripStatus.COMPLETED) {
            throw new ConflictException("Yolculuk tamamlanmış ve iptal edilemez.");
        }
        else if(trip.getTripStatus() == TripStatus.CANCELLED) {
            throw new ConflictException("Yolculuk zaten iptal edilmiş.");
        }
        else {
            trip.setTripStatus(TripStatus.CANCELLED);
            trip.setEndedAt(Timestamp.from(Instant.now()));
            tripsRepository.save(trip);
            TripMessage kafkaMessage = new TripMessage(
                    "TRIP_CANCELED",
                    trip.getTripId(),
                    trip.getDriver() != null ? trip.getDriver().getUserId() : null,
                    Timestamp.valueOf(LocalDateTime.now())
            );
            kafkaTemplate.send(TOPIC_TRIP_EVENTS, kafkaMessage);
        }
    }
}

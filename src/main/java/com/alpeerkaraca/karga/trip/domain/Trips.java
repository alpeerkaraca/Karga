package com.alpeerkaraca.karga.trip.domain;

import com.alpeerkaraca.karga.user.domain.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trips")
public class Trips {
    @Id
    @GeneratedValue
    private UUID tripId;
    private double startLatitude;
    private double startLongitude;
    private String startAddress;
    private double endLatitude;
    private double endLongitude;
    private String endAddress;
    private Timestamp requestedAt;
    private Timestamp startedAt;
    private Timestamp endedAt;
    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus;
    private BigDecimal fare;
    @NotNull
    @ManyToOne
    private Users passenger;
    @ManyToOne
    private Users driver;
}

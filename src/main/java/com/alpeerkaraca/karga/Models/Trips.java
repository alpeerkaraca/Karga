package com.alpeerkaraca.karga.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
@Setter
@Getter
@Entity
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

}

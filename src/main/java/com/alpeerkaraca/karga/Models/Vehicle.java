package com.alpeerkaraca.karga.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
@Entity
public class Vehicle {

    @Id
    @GeneratedValue
    private UUID vehicleId;
    private String brand;
    private String model;
    @Column(unique = true, nullable = false)
    private String plate;
    private String color;
    private String year;

}

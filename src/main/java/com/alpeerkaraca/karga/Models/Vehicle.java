package com.alpeerkaraca.karga.Models;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle extends BaseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID vehicleId;

    private String brand;
    private String model;
    @Column(unique = true, nullable = false)
    private String plate;
    private String color;
    private String year;

}

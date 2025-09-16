package com.alpeerkaraca.karga.Repositories;

import com.alpeerkaraca.karga.Models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripsRepository extends JpaRepository<Vehicle, UUID> {
}

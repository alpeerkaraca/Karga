package com.alpeerkaraca.karga.Repositories;

import com.alpeerkaraca.karga.Models.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentsRepository extends JpaRepository<Payments, UUID> {
}

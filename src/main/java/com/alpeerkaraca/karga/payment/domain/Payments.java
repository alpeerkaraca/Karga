package com.alpeerkaraca.karga.payment.domain;

import com.alpeerkaraca.karga.user.domain.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payments {
    @Id
    @GeneratedValue
    private UUID paymentId;
    private BigDecimal paymentAmount;
    private Timestamp paidAt;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String paymentUrl;
    private UUID tripId;
    private UUID passengerId;
}

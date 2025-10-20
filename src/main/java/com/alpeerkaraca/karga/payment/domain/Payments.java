package com.alpeerkaraca.karga.payment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@ToString
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

}

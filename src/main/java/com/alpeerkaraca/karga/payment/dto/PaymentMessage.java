package com.alpeerkaraca.karga.payment.dto;

import com.alpeerkaraca.karga.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentMessage(
        UUID paymentId,
        BigDecimal paymentFare,
        PaymentStatus paymentStatus
) {
}

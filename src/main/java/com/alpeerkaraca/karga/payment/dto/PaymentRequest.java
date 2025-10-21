package com.alpeerkaraca.karga.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String currency
) {
}

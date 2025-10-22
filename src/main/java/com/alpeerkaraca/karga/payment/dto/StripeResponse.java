package com.alpeerkaraca.karga.payment.dto;

import com.alpeerkaraca.karga.payment.domain.PaymentStatus;

public record StripeResponse(
        String status,
        String message,
        String sessionId,
        String sessionUrl
) {
}

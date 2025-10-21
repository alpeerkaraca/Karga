package com.alpeerkaraca.karga.payment.infra.kafka;

import com.alpeerkaraca.karga.payment.application.PaymentService;
import com.alpeerkaraca.karga.payment.domain.PaymentStatus;
import com.alpeerkaraca.karga.payment.dto.PaymentMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {
    private static final String TOPIC_PENDING_PAYMENTS = "pending_payments";
    private final PaymentService stripePaymentService;

    @KafkaListener(topics = TOPIC_PENDING_PAYMENTS, groupId = "payments")
    public void handlePayment(PaymentMessage message) {
        if (message.paymentStatus() != PaymentStatus.PENDING) {
            log.warn("PENDING olmayan bir ödeme mesajı alındı, ihmal ediliyor: {}", message.paymentId());
            return;
        }

        log.debug("Pending ödeme mesajı alındı, Stripe oturumu oluşturuluyor: {}", message.paymentId());
        stripePaymentService.createCheckoutSessionForPayment(message.paymentId());

    }

}

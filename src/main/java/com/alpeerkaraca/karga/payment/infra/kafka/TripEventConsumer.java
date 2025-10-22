package com.alpeerkaraca.karga.payment.infra.kafka;

import com.alpeerkaraca.karga.payment.domain.PaymentStatus;
import com.alpeerkaraca.karga.payment.domain.Payments;
import com.alpeerkaraca.karga.payment.domain.PaymentsRepository;
import com.alpeerkaraca.karga.payment.dto.PaymentMessage;
import com.alpeerkaraca.karga.trip.application.TripManagementService;
import com.alpeerkaraca.karga.trip.domain.Trips;
import com.alpeerkaraca.karga.trip.dto.TripMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripEventConsumer {
    private static final String TOPIC_TRIP_EVENTS = "trip_events";
    private final KafkaTemplate<String, PaymentMessage> kafkaTemplate;
    private final PaymentsRepository paymentsRepository;

    @KafkaListener(topics = TOPIC_TRIP_EVENTS, groupId = "payment-creation-group")
    public void handleTripEvent(TripMessage message) {
            String eventType = message.eventType();
            if(!"TRIP_COMPLETED".equals(eventType))
                return ;
            log.debug("Tamamlanan yolculuk olayı alındı, PENDING ödeme oluşturuluyor: {}", message.tripId());
            Payments payment = Payments.builder()
                    .paymentAmount(message.fare())
                    .paymentStatus(PaymentStatus.PENDING)
                    .tripId(message.tripId())
                    .passengerId(message.passengerId())
                    .build();

            Payments savedPayment = paymentsRepository.save(payment);

            PaymentMessage paymentMessage = new PaymentMessage(
                    savedPayment.getPaymentId(),
                    savedPayment.getPaymentAmount(),
                    savedPayment.getPaymentStatus()
            );
            kafkaTemplate.send("pending_payments", paymentMessage);
    }
}

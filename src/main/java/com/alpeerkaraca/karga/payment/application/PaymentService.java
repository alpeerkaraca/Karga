package com.alpeerkaraca.karga.payment.application;

import com.alpeerkaraca.karga.core.exception.ResourceNotFoundException;
import com.alpeerkaraca.karga.payment.domain.PaymentStatus;
import com.alpeerkaraca.karga.payment.domain.Payments;
import com.alpeerkaraca.karga.payment.domain.PaymentsRepository;
import com.alpeerkaraca.karga.payment.dto.PaymentRequest;
import com.alpeerkaraca.karga.payment.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {


    private final PaymentsRepository paymentsRepository;

    @Transactional
    public StripeResponse createCheckoutSessionForPayment(UUID paymentId) {

        Payments payment = paymentsRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ödeme kaydı bulunamadı: " + paymentId));

        if(payment.getPaymentStatus() != PaymentStatus.PENDING) {
            log.warn("Bu ödeme için zaten işlem yapılmış: {}", paymentId);
            return null;
        }

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.
                LineItem.PriceData.
                ProductData.builder().setName("Karga Yolculuk Ödemesi: " + payment.getTripId()).build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("TRY")
                .setUnitAmountDecimal(payment.getPaymentAmount())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8081/success?payment_id=" + payment.getPaymentId())
                .setCancelUrl("http://localhost:8081/cancel?payment_id=" + payment.getPaymentId())
                .addLineItem(lineItem)
                .setClientReferenceId(payment.getPaymentId().toString())
                .putMetadata("trip_id", payment.getTripId().toString())
                .putMetadata("user_id", payment.getPassengerId().toString())
                .build();

        try {
            Session session = Session.create(params);

            payment.setTransactionId(session.getId());
            payment.setPaymentUrl(session.getUrl());
            paymentsRepository.save(payment);
            log.info("Stripe oturumu başarıyla oluşturuldu: {}", session.getId());
            return new StripeResponse("CREATED", "Session oluşturuldu.", session.getId(), session.getUrl());

        } catch (StripeException ex) {
            log.error("Stripe ile ödeme oluşturulurken hata: {}", ex.getMessage());
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentsRepository.save(payment);

            throw new RuntimeException("Stripe oturumu oluşturulamadı: " + ex.getMessage());

        }
    }
}

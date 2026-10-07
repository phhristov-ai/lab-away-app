package com.labaway.backend.strategy;

import com.labaway.backend.configuration.properties.StripeProperties;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.exception.PaymentException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentStrategy implements PaymentStrategy {

    private final StripeProperties stripeProperties;

    @Autowired
    public StripePaymentStrategy(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.secretKey();
    }
    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.STRIPE;
    }

    @Override
    public CreatePaymentResponseDto initiatePayment(CreatePaymentRequestDto request) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.amount())
                    .setCurrency(request.currency())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return new CreatePaymentResponseDto(
                    intent.getId(),
                    intent.getClientSecret()
            );
        }
        catch (StripeException e) {
            throw new PaymentException("Failed to create payment intent", e);
        }
    }

    @Override
    public boolean isPaymentCompleted(String sessionId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(sessionId);
            return "succeeded".equals(intent.getStatus());
        } catch (StripeException e) {
            throw new PaymentException("Failed to verify Stripe payment", e);
        }
    }
}

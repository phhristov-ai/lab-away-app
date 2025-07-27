package com.labaway.backend.strategy;

import com.labaway.backend.configuration.PayPalConfig;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.exception.PayPalServiceException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PayPalPaymentStrategy implements PaymentStrategy {

    private final PayPalHttpClient payPalClient;

    public PayPalPaymentStrategy(PayPalConfig payPalConfig) {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                payPalConfig.getClientId(),
                payPalConfig.getClientSecret()
        );

        this.payPalClient = new PayPalHttpClient(environment);
    }

    public PayPalHttpClient getPayPalClient() {
        return payPalClient;
    }

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PAYPAL;
    }

    @Override
    public CreatePaymentResponseDto initiatePayment(CreatePaymentRequestDto request) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode(request.getCurrency())
                .value(String.valueOf(request.getAmount() / 100.0));

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest().amountWithBreakdown(amount);
        orderRequest.purchaseUnits(List.of(purchaseUnit));

        OrdersCreateRequest paypalRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> response = payPalClient.execute(paypalRequest);
            return CreatePaymentResponseDto.builder()
                    .paymentIntentId(response.result().id())
                    .build();
        } catch (IOException e) {
            throw new PayPalServiceException("Failed to create PayPal order", e);        }
    }

    @Override
    public boolean isPaymentCompleted(String orderId) {
        OrdersGetRequest request = new OrdersGetRequest(orderId);
        try {
            HttpResponse<Order> response = payPalClient.execute(request);
            String status = response.result().status();
            return "COMPLETED".equalsIgnoreCase(status);
        } catch (IOException e) {
            throw new PayPalServiceException("Failed to verify PayPal order", e);        }
    }
}

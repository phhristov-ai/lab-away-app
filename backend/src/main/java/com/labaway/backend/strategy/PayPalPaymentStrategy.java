package com.labaway.backend.strategy;

import com.labaway.backend.configuration.properties.PaypalProperties;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.exception.PayPalServiceException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class PayPalPaymentStrategy implements PaymentStrategy {

    private final PayPalHttpClient payPalClient;

    public PayPalPaymentStrategy(PaypalProperties paypalProperties) {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                paypalProperties.clientId(),
                paypalProperties.secret()
        );

        this.payPalClient = new PayPalHttpClient(environment);
    }

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PAYPAL;
    }

    @Override
    public CreatePaymentResponseDto initiatePayment(CreatePaymentRequestDto request) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        BigDecimal value  = BigDecimal.valueOf(request.amount())
                .movePointLeft(2);

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode(request.currency())
                .value(value.toPlainString());

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest().amountWithBreakdown(amount);
        orderRequest.purchaseUnits(List.of(purchaseUnit));

        OrdersCreateRequest paypalRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> response = payPalClient.execute(paypalRequest);
            return new CreatePaymentResponseDto(
                    response.result().id(),
                    null
            );
        } catch (IOException e) {
            throw new PayPalServiceException("Failed to create PayPal order", e);        }
    }

    @Override
    public boolean isPaymentCompleted(String orderId) {
        OrdersGetRequest request = new OrdersGetRequest(orderId);
        try {
            HttpResponse<Order> response = payPalClient.execute(request);
            Order order = response.result();
            return order != null
                    && "COMPLETED".equals(order.status());
        } catch (IOException e) {
            throw new PayPalServiceException("Failed to verify PayPal order", e);        }
    }
}

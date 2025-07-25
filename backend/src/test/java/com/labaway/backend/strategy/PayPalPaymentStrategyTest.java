package com.labaway.backend.strategy;

import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.exception.PayPalServiceException;
import com.labaway.backend.strategy.PayPalPaymentStrategy;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpRequest;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersGetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayPalPaymentStrategyTest {

    @InjectMocks
    private PayPalPaymentStrategy payPalPaymentStrategy;

    @Mock
    private PayPalHttpClient mockPayPalClient;

    @Mock
    private HttpResponse<Order> mockResponse;

    @Mock
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        injectTestFields();
    }

    @Test
    void shouldInitiatePaymentSuccessfully() throws Exception {
        CreatePaymentRequestDto request = createPaymentRequestDto();

        mockSuccessfulPayPalExecution();

        CreatePaymentResponseDto response = payPalPaymentStrategy.initiatePayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getPaymentIntentId()).isEqualTo("order_test_123");

        verify(mockPayPalClient).execute(ArgumentMatchers.<HttpRequest<Order>>any());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenPayPalFails() throws Exception {
        CreatePaymentRequestDto request = createPaymentRequestDto();

        when(mockPayPalClient.execute(ArgumentMatchers.<HttpRequest<Order>>any()))
                .thenThrow(new IOException("API failure"));

        assertThatThrownBy(() -> payPalPaymentStrategy.initiatePayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create PayPal order");

        verify(mockPayPalClient).execute(ArgumentMatchers.<HttpRequest<Order>>any());
    }

    @Test
    void isPaymentCompleted_shouldReturnTrue_whenStatusIsCompleted() throws IOException {
        String orderId = "test-order-id";
        when(mockPayPalClient.execute(any(OrdersGetRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(mockOrder);
        when(mockOrder.status()).thenReturn("COMPLETED");

        boolean result = payPalPaymentStrategy.isPaymentCompleted(orderId);

        assertThat(result).isTrue();
        verify(mockPayPalClient).execute(any(OrdersGetRequest.class));
    }

    @Test
    void isPaymentCompleted_shouldReturnFalse_whenStatusIsNotCompleted() throws IOException {
        String orderId = "test-order-id";
        when(mockPayPalClient.execute(any(OrdersGetRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(mockOrder);
        when(mockOrder.status()).thenReturn("PENDING");

        boolean result = payPalPaymentStrategy.isPaymentCompleted(orderId);

        assertThat(result).isFalse();
    }

    @Test
    void isPaymentCompleted_shouldThrowException_whenPayPalFails() throws IOException {
        String orderId = "test-order-id";
        when(mockPayPalClient.execute(any(OrdersGetRequest.class))).thenThrow(new IOException("API down"));

        assertThatThrownBy(() -> payPalPaymentStrategy.isPaymentCompleted(orderId))
                .isInstanceOf(PayPalServiceException.class)
                .hasMessageContaining("Failed to verify PayPal order");
    }

    private void injectTestFields() {
        ReflectionTestUtils.setField(payPalPaymentStrategy, "clientId", "test-client-id");
        ReflectionTestUtils.setField(payPalPaymentStrategy, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(payPalPaymentStrategy, "payPalClient", mockPayPalClient);
    }

    private CreatePaymentRequestDto createPaymentRequestDto() {
        return CreatePaymentRequestDto.builder()
                .amount(1000L)
                .currency("EUR")
                .build();
    }

    private void mockSuccessfulPayPalExecution() throws Exception {
        when(mockPayPalClient.execute(ArgumentMatchers.<HttpRequest<Order>>any())).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(mockOrder);
        when(mockOrder.id()).thenReturn("order_test_123");
    }
}

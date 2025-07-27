package com.labaway.backend.strategy;

import com.labaway.backend.configuration.PayPalConfig;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.exception.PayPalServiceException;
import com.paypal.core.AuthorizationProvider;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpRequest;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCreateRequest;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayPalPaymentStrategyTest {
    @InjectMocks
    private PayPalPaymentStrategy payPalPaymentStrategy;
    @Mock
    private PayPalHttpClient mockPayPalClient;
    @Mock
    private PayPalConfig payPalConfig;
    @Mock
    private HttpResponse<Order> mockResponse;
    @Mock
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(payPalConfig.getClientId()).thenReturn("test-client-id");
        when(payPalConfig.getClientSecret()).thenReturn("test-client-secret");

        payPalPaymentStrategy = new PayPalPaymentStrategy(payPalConfig);
    }

    @Test
    void shouldInitiatePaymentSuccessfully() throws Exception {
        CreatePaymentRequestDto request = createPaymentRequestDto();

        PayPalHttpClient mockPayPalClient = mock(PayPalHttpClient.class);
        HttpResponse<Order> mockResponse = mock(HttpResponse.class);
        Order mockOrder = mock(Order.class);

        when(mockPayPalClient.execute(any(OrdersCreateRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(mockOrder);
        when(mockOrder.id()).thenReturn("order_test_123");

        ReflectionTestUtils.setField(payPalPaymentStrategy, "payPalClient", mockPayPalClient);

        CreatePaymentResponseDto response = payPalPaymentStrategy.initiatePayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getPaymentIntentId()).isEqualTo("order_test_123");

        verify(mockPayPalClient).execute(any(OrdersCreateRequest.class));
    }


    @Test
    void shouldThrowRuntimeExceptionWhenPayPalFails() throws Exception {
        CreatePaymentRequestDto request = createPaymentRequestDto();

        PayPalPaymentStrategy strategy = new PayPalPaymentStrategy(payPalConfig);
        ReflectionTestUtils.setField(strategy, "payPalClient", mockPayPalClient);

        when(mockPayPalClient.execute(any(HttpRequest.class)))
                .thenThrow(new IOException("API failure"));

        assertThatThrownBy(() -> strategy.initiatePayment(request))
                .isInstanceOf(PayPalServiceException.class)
                .hasMessageContaining("Failed to create PayPal order");

        verify(mockPayPalClient).execute(any(HttpRequest.class));
    }


    @Test
    void isPaymentCompleted_shouldReturnTrue_whenStatusIsCompleted() throws IOException {
        String orderId = "test-order-id";

        AuthorizationProvider mockAuthorizationProvider = mock(AuthorizationProvider.class);

        PayPalHttpClient mockPayPalHttpClient = mock(PayPalHttpClient.class);

        when(mockPayPalHttpClient.execute(any(OrdersGetRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(mockOrder);
        when(mockOrder.status()).thenReturn("COMPLETED");

        PayPalPaymentStrategy payPalPaymentStrategy = new PayPalPaymentStrategy(payPalConfig);
        ReflectionTestUtils.setField(payPalPaymentStrategy, "payPalClient", mockPayPalHttpClient);

        boolean result = payPalPaymentStrategy.isPaymentCompleted(orderId);

        assertThat(result).isTrue();

        verify(mockPayPalHttpClient).execute(any(OrdersGetRequest.class));
    }

    @Test
    void isPaymentCompleted_shouldReturnFalse_whenStatusIsNotCompleted() throws IOException {
        String orderId = "test-order-id";

        PayPalHttpClient mockPayPalHttpClient = mock(PayPalHttpClient.class);
        HttpResponse<Order> mockResponse = mock(HttpResponse.class);
        Order mockOrder = mock(Order.class);

        when(mockResponse.result()).thenReturn(mockOrder);
        when(mockOrder.status()).thenReturn("PENDING");
        when(mockPayPalHttpClient.execute(any(OrdersGetRequest.class))).thenReturn(mockResponse);

        PayPalPaymentStrategy payPalPaymentStrategy = new PayPalPaymentStrategy(payPalConfig);
        ReflectionTestUtils.setField(payPalPaymentStrategy, "payPalClient", mockPayPalHttpClient);

        boolean result = payPalPaymentStrategy.isPaymentCompleted(orderId);

        assertThat(result).isFalse();
    }


    @Test
    void isPaymentCompleted_shouldThrowException_whenPayPalFails() throws IOException {
        String orderId = "test-order-id";

        PayPalHttpClient mockClient = mock(PayPalHttpClient.class);
        when(mockClient.execute(any(OrdersGetRequest.class))).thenThrow(new IOException("API down"));

        ReflectionTestUtils.setField(payPalPaymentStrategy, "payPalClient", mockClient);

        assertThatThrownBy(() -> payPalPaymentStrategy.isPaymentCompleted(orderId))
                .isInstanceOf(PayPalServiceException.class)
                .hasMessageContaining("Failed to verify PayPal order");
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

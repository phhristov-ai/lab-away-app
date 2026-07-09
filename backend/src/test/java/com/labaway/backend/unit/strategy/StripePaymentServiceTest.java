package com.labaway.backend.unit.strategy;

import com.labaway.backend.configuration.payment.StripeConfig;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.strategy.StripePaymentStrategy;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StripePaymentServiceTest {

    @InjectMocks
    private StripePaymentStrategy stripePaymentStrategy;

    @Mock
    private PaymentIntent paymentIntent;

    @Mock
    private StripeConfig stripeConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stripeConfig.getSecretKey()).thenReturn("sk_test_123");
    }

    @Test
    void shouldInitiatePaymentSuccessfully() {
        CreatePaymentRequestDto request = createPaymentRequestDto();

        PaymentIntent mockIntent = createMockPaymentIntent();

        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockIntent);

            CreatePaymentResponseDto response = stripePaymentStrategy.initiatePayment(request);

            assertThat(response).isNotNull();
            assertThat(response.paymentIntentId()).isEqualTo("pi_test_123");
            assertThat(response.clientSecret()).isEqualTo("secret_test_456");
        }
    }
    private static CreatePaymentRequestDto createPaymentRequestDto() {
        return new CreatePaymentRequestDto(
                500L,
                "eur",
                "test@example.com",
                "https://success.com",
                "https://cancel.com",
                null
        );
    }

    private static PaymentIntent createMockPaymentIntent() {
        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getId()).thenReturn("pi_test_123");
        when(mockIntent.getClientSecret()).thenReturn("secret_test_456");
        return mockIntent;
    }
}

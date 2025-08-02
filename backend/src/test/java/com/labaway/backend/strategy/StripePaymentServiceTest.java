package com.labaway.backend.strategy;

import com.labaway.backend.configuration.StripeConfig;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
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
            assertThat(response.getPaymentIntentId()).isEqualTo("pi_test_123");
            assertThat(response.getClientSecret()).isEqualTo("secret_test_456");
        }
    }
    private static CreatePaymentRequestDto createPaymentRequestDto() {
        return CreatePaymentRequestDto.builder()
                .amount(500L)
                .currency("eur")
                .customerEmail("test@example.com")
                .successUrl("https://success.com")
                .cancelUrl("https://cancel.com")
                .build();
    }

    private static PaymentIntent createMockPaymentIntent() {
        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getId()).thenReturn("pi_test_123");
        when(mockIntent.getClientSecret()).thenReturn("secret_test_456");
        return mockIntent;
    }
}

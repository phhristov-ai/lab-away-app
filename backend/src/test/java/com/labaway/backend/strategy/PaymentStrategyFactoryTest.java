package com.labaway.backend.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStrategyFactoryTest {

    @Mock
    private PaymentStrategy stripeStrategy;
    @Mock
    private PaymentStrategy paypalStrategy;

    private PaymentStrategyFactory factory;

    @BeforeEach
    public void setUp() {
        when(stripeStrategy.getProvider()).thenReturn(PaymentProvider.STRIPE);
        when(paypalStrategy.getProvider()).thenReturn(PaymentProvider.PAYPAL);

        factory = new PaymentStrategyFactory(List.of(stripeStrategy, paypalStrategy));
    }

    @Test
    void shouldReturnStripeStrategy() {
        PaymentStrategy strategy = factory.getStrategy(PaymentProvider.STRIPE);
        assertThat(strategy).isSameAs(stripeStrategy);
    }

    @Test
    void shouldReturnPayPalStrategy() {
        PaymentStrategy strategy = factory.getStrategy(PaymentProvider.PAYPAL);
        assertThat(strategy).isSameAs(paypalStrategy);
    }

    @Test
    void shouldThrowWhenProviderIsNull() {
        assertThatThrownBy(() -> factory.getStrategy(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported payment provider");
    }

    @Test
    void shouldThrowWhenProviderIsInvalid() {
        assertThatThrownBy(() -> factory.getStrategy(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}

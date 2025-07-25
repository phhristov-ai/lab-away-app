package com.labaway.backend.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;

@Component
public class PaymentStrategyFactory {

    private final EnumMap<PaymentProvider, PaymentStrategy> strategies;

    @Autowired
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        strategies = new EnumMap<>(PaymentProvider.class);
        for (PaymentStrategy strategy : strategyList) {
            strategies.put(strategy.getProvider(), strategy);
        }
    }

    public PaymentStrategy getStrategy(PaymentProvider provider) {
        PaymentStrategy strategy = strategies.get(provider);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment provider: " + provider);
        }
        return strategy;
    }
}

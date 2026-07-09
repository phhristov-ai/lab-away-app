package com.labaway.backend.unit.util;

import com.labaway.backend.util.OrderNumberGenerator;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderNumberGeneratorTest {

    private final OrderNumberGenerator generator = new OrderNumberGenerator();

    private static final int EXPECTED_LENGTH = 6;
    private static final String VALID_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Test
    void generate_shouldReturnNonNullAndCorrectLength() {
        String orderNumber = generator.generate();

        assertNotNull(orderNumber, "Order number should not be null");
        assertEquals(EXPECTED_LENGTH, orderNumber.length(), "Order number should have length of 6");
    }

    @Test
    void generate_shouldOnlyContainValidCharacters() {
        String orderNumber = generator.generate();

        for (char c : orderNumber.toCharArray()) {
            assertTrue(VALID_CHARACTERS.indexOf(c) >= 0,
                    "Character '" + c + "' is not allowed in order number");
        }
    }

    @RepeatedTest(5)
    void generate_shouldProduceMostlyUniqueValues() {
        Set<String> values = new HashSet<>();
        int duplicates = 0;
        int attempts = 1000;

        for (int i = 0; i < attempts; i++) {
            String generated = generator.generate();
            if (!values.add(generated)) {
                duplicates++;
            }
        }
        double duplicateRatio = (double) duplicates / attempts;
        assertTrue(duplicateRatio < 0.001, "Too many duplicates: " + duplicates);
    }

}
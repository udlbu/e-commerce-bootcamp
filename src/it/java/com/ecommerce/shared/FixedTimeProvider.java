package com.ecommerce.shared;

import com.ecommerce.shared.domain.TimeProvider;

import java.time.Instant;

import static com.ecommerce.IntegrationTestData.DATE;

public class FixedTimeProvider implements TimeProvider {
    @Override
    public Instant now() {
        return Instant.parse(DATE);
    }
}

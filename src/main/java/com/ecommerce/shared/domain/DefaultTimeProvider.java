package com.ecommerce.shared.domain;

import java.time.Instant;

public class DefaultTimeProvider implements TimeProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}

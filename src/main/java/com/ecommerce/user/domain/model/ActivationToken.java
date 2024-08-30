package com.ecommerce.user.domain.model;

import java.time.Instant;

public record ActivationToken(Long userId, String token, Instant createdAt) {
}

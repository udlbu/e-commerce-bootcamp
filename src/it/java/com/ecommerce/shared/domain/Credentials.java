package com.ecommerce.shared.domain;

import lombok.Builder;

@Builder
public record Credentials(String username, String password) {
}

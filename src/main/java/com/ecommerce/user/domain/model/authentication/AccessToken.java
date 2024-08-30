package com.ecommerce.user.domain.model.authentication;

public record AccessToken(String token, long expiresIn, String refreshToken, long refreshExpiresIn) {
}

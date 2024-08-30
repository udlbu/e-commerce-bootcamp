package com.ecommerce.shared.domain.exception;

import org.springframework.security.core.AuthenticationException;

public class UserRemovedFromSystemException extends AuthenticationException {
    public UserRemovedFromSystemException(String message) {
        super(message);
    }
}

package com.ecommerce.shared.domain.exception;

public class ConcurrentModification extends RuntimeException {
    public ConcurrentModification() {
    }

    public ConcurrentModification(String message) {
        super(message);
    }

    public ConcurrentModification(String message, Throwable cause) {
        super(message, cause);
    }
}

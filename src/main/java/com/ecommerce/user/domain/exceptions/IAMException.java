package com.ecommerce.user.domain.exceptions;

public class IAMException extends RuntimeException {

    public IAMException() {
        super("Unexpected problem with authorization server");
    }

    public IAMException(String message) {
        super(message);
    }
}

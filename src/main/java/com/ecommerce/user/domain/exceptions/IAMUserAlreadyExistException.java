package com.ecommerce.user.domain.exceptions;

public class IAMUserAlreadyExistException extends IAMException {
    public IAMUserAlreadyExistException(String message) {
        super(message);
    }
}

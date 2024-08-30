package com.ecommerce.offer.domain.exceptions;

public class OfferCannotBeActivatedException extends RuntimeException {
    public OfferCannotBeActivatedException(String message) {
        super(message);
    }
}

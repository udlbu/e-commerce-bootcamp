package com.ecommerce.offer.domain.exceptions;

public class OfferCannotBeDeletedException extends RuntimeException {
    public OfferCannotBeDeletedException(String message) {
        super(message);
    }
}

package com.ecommerce.offer.domain.exceptions;

public class OfferCannotBeModifiedException extends RuntimeException {
    public OfferCannotBeModifiedException(String message) {
        super(message);
    }
}

package com.ecommerce.order.domain.exceptions;

public class InactiveOfferException extends RuntimeException {

    public InactiveOfferException(String message) {
        super(message);
    }
}

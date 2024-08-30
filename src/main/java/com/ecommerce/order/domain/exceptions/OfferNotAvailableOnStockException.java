package com.ecommerce.order.domain.exceptions;

public class OfferNotAvailableOnStockException extends RuntimeException {

    public OfferNotAvailableOnStockException(String message) {
        super(message);
    }
}

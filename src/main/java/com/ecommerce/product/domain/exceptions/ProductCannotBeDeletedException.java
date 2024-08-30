package com.ecommerce.product.domain.exceptions;

public class ProductCannotBeDeletedException extends RuntimeException {
    public ProductCannotBeDeletedException(String message) {
        super(message);
    }
}

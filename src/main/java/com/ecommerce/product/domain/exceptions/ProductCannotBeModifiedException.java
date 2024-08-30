package com.ecommerce.product.domain.exceptions;

public class ProductCannotBeModifiedException extends RuntimeException {
    public ProductCannotBeModifiedException(String message) {
        super(message);
    }
}

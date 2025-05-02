package com.banreservas.micstockservice.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final String productId) {
        super(String.format("Product with id: '%s' not exist", productId));
    }
}

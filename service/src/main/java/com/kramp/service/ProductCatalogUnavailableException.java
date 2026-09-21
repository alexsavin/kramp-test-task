package com.kramp.service;

public class ProductCatalogUnavailableException extends RuntimeException {

    public ProductCatalogUnavailableException(String productId) {
        super("Product catalog information is unavailable for product ID: " + productId);
    }
}

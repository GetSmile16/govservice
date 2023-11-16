package org.example.exception.product;

public class ProductIsOver extends RuntimeException {
    public ProductIsOver(String message) {
        super(message);
    }
}

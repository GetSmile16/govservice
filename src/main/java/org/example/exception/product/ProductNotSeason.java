package org.example.exception.product;

public class ProductNotSeason extends RuntimeException {
    public ProductNotSeason(String message) {
        super(message);
    }
}

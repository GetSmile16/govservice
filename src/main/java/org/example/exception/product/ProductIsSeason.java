package org.example.exception.product;

public class ProductIsSeason extends RuntimeException {
    public ProductIsSeason(String message) {
        super(message);
    }
}

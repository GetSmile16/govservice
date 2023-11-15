package org.example.exception.product;

public class ProductIsDone extends RuntimeException {
    public ProductIsDone(String message) {
        super(message);
    }
}

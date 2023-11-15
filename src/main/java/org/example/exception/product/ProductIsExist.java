package org.example.exception.product;

public class ProductIsExist extends RuntimeException {
    public ProductIsExist(String message) {
        super(message);
    }
}

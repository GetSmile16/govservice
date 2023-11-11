package org.example.exception;

public class ExpiredToken extends RuntimeException {
    public ExpiredToken(String message) {
        super(message);
    }
}

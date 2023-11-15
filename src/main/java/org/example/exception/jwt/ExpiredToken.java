package org.example.exception.jwt;

public class ExpiredToken extends RuntimeException {
    public ExpiredToken(String message) {
        super(message);
    }
}

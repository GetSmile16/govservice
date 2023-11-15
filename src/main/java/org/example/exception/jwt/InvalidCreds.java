package org.example.exception.jwt;

public class InvalidCreds extends RuntimeException {
    public InvalidCreds(String message) {
        super(message);
    }
}

package org.example.exception;

public class InvalidCreds extends RuntimeException {
    public InvalidCreds(String message) {
        super(message);
    }
}

package org.example.exception;

public class WrongToken extends RuntimeException {
    public WrongToken(String message) {
        super(message);
    }
}

package org.example.exception.jwt;

public class WrongToken extends RuntimeException {
    public WrongToken(String message) {
        super(message);
    }
}

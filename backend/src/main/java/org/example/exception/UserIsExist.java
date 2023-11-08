package org.example.exception;

public class UserIsExist extends RuntimeException {
    public UserIsExist(String message) {
        super(message);
    }
}

package org.example.exception.user;

public class UserIsExist extends RuntimeException {
    public UserIsExist(String message) {
        super(message);
    }
}

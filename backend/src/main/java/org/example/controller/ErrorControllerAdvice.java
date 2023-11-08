package org.example.controller;

import org.example.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserIsExist.class)
    public ResponseEntity<Map<String, Object>> handleUserIsExist(Exception ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 1, ex);
    }

    @ExceptionHandler(WrongToken.class)
    public ResponseEntity<Map<String, Object>> handleWrongToken(Exception ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 2, ex);
    }

    @ExceptionHandler(ExpiredToken.class)
    public ResponseEntity<Map<String, Object>> handleExpiredToken(Exception ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 3, ex);
    }

    @ExceptionHandler(InvalidCreds.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCreds(Exception ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 4, ex);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(Exception ex) {
        return handleCustomException(HttpStatus.NOT_FOUND, 5, ex);
    }

    //Формирует кастомное тело ответа на запрос клиента с кодом и текстом ошибки
    protected Map<String, Object> body(int code, Exception exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", exception.getMessage());
        return map;
    }

    protected ResponseEntity<Map<String, Object>> handleCustomException(HttpStatus status,
                                                                        int code, Exception exception) {
        return ResponseEntity.status(status).body(body(code, exception));
    }
}

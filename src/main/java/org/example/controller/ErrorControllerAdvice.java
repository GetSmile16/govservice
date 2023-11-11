package org.example.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.example.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {
    //Violations
    public class Violation {

        private String fieldName;
        private String message;

        public Violation(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getMessage() {
            return message;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<Violation>> onConstraintValidationException(ConstraintViolationException ex) {
        return handleValidationExceptions(ex);
    }

    protected List<Violation> bodyViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getInvalidValue().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
    }

    protected ResponseEntity<List<Violation>> handleValidationExceptions(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bodyViolations(ex));
    }

    // Exceptions
    @ExceptionHandler(UserIsExist.class)
    public ResponseEntity<Map<String, Object>> handleUserIsExist(UserIsExist ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 1, ex);
    }

    @ExceptionHandler(WrongToken.class)
    public ResponseEntity<Map<String, Object>> handleWrongToken(WrongToken ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 2, ex);
    }

    @ExceptionHandler(ExpiredToken.class)
    public ResponseEntity<Map<String, Object>> handleExpiredToken(ExpiredToken ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 3, ex);
    }

    @ExceptionHandler(InvalidCreds.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCreds(InvalidCreds ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 4, ex);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFound ex) {
        return handleCustomException(HttpStatus.NOT_FOUND, 5, ex);
    }

    protected Map<String, Object> bodyError(int code, Exception exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", exception.getMessage());
        return map;
    }

    protected ResponseEntity<Map<String, Object>> handleCustomException(HttpStatus status,
                                                                        int code, Exception exception) {
        return ResponseEntity.status(status).body(bodyError(code, exception));
    }
}

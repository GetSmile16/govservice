package org.example.controller;

import jakarta.validation.ConstraintViolationException;
import org.example.exception.jwt.ExpiredToken;
import org.example.exception.jwt.InvalidCreds;
import org.example.exception.jwt.WrongToken;
import org.example.exception.product.*;
import org.example.exception.user.UserIsExist;
import org.example.exception.user.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {
    //Violations
    public static class Violation {

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

    public static class Error {

        private Integer code;
        private String message;

        public Error(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
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
    public ResponseEntity<Error> handleUserIsExist(UserIsExist ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 1, ex);
    }

    @ExceptionHandler(WrongToken.class)
    public ResponseEntity<Error> handleWrongToken(WrongToken ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 2, ex);
    }

    @ExceptionHandler(ExpiredToken.class)
    public ResponseEntity<Error> handleExpiredToken(ExpiredToken ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 3, ex);
    }

    @ExceptionHandler(InvalidCreds.class)
    public ResponseEntity<Error> handleInvalidCreds(InvalidCreds ex) {
        return handleCustomException(HttpStatus.UNAUTHORIZED, 4, ex);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Error> handleUserNotFound(UserNotFound ex) {
        return handleCustomException(HttpStatus.NOT_FOUND, 5, ex);
    }

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<Error> handleProductNotFound(ProductNotFound ex) {
        return handleCustomException(HttpStatus.NOT_FOUND, 6, ex);
    }

    @ExceptionHandler(ProductIsDone.class)
    public ResponseEntity<Error> handleProductIsDone(ProductIsDone ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 7, ex);
    }

    @ExceptionHandler(ProductNotSeason.class)
    public ResponseEntity<Error> handleProductNotSeason(ProductNotSeason ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 8, ex);
    }

    @ExceptionHandler(ProductIsSeason.class)
    public ResponseEntity<Error> handleProductIsSeason(ProductIsSeason ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 9, ex);
    }

    @ExceptionHandler(ProductIsExist.class)
    public ResponseEntity<Error> handleProductIsExist(ProductIsExist ex) {
        return handleCustomException(HttpStatus.BAD_REQUEST, 10, ex);
    }

    protected Error bodyError(int code, Exception exception) {
        return new Error(
                code,
                exception.getMessage()
        );
    }

    protected ResponseEntity<Error> handleCustomException(HttpStatus status,
                                                          int code, Exception exception) {
        return ResponseEntity.status(status).body(bodyError(code, exception));
    }

    @ExceptionHandler(ProductIsOver.class)
    public ResponseEntity<String> handleProductIsOver(ProductIsOver ex) {
        return ResponseEntity.ok(ex.getMessage());
    }
}

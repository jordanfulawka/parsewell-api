package com.jordanfulawka.parsewell.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiExceptionResponse> handleException(EntityNotFoundException e) {

        ApiExceptionResponse error = new ApiExceptionResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(e.getMessage());
        error.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler
    public ResponseEntity<ApiExceptionResponse> handleUserAlreadyExists(UserAlreadyExistsException e) {

        ApiExceptionResponse error = new ApiExceptionResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError(e.getMessage());
        error.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);

    }

    @ExceptionHandler
    public ResponseEntity<ApiExceptionResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        ApiExceptionResponse error = new ApiExceptionResponse();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setError(e.getMessage());
        error.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

}

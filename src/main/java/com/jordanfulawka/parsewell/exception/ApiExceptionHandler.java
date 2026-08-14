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

    @ExceptionHandler(JobFetchException.class)
    public ResponseEntity<ApiExceptionResponse> handleJobFetch(JobFetchException e) {

        HttpStatus status = switch(e.getReason()) {
            case BLOCKED, NO_CONTENT, NOT_FOUND -> HttpStatus.UNPROCESSABLE_CONTENT;
            case UNREACHABLE -> HttpStatus.BAD_GATEWAY;
        };

        // POSSIBLE MAKE NEW EXCEPTION TYPE IN FUTURE THAT DIRECTLY RETURNS ENUM INSTEAD OF STATUS CODE.
        // BLOCKED, NO_CONTENT, NOT_FOUND GIVES MORE INFO THAN CONDENSING THEM ALL DOWN TO A 422.
        String message = switch(e.getReason()) {
            case BLOCKED -> "This site blocks automated access. Paste the job description below and we'll take it from there.";
            case NOT_FOUND -> "That posting couldn't be found — it may have been taken down. Check the link, or paste the description manually.";
            case NO_CONTENT -> "This posting loads its content dynamically, so we couldn't read it. Paste the description below to continue.";
            case UNREACHABLE -> "We couldn't reach that page. Check the link and try again, or paste the description manually.";
        };

        return ResponseEntity.status(status).body(new ApiExceptionResponse(status.value(), message, System.currentTimeMillis()));
    }

}

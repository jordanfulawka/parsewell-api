package com.jordanfulawka.parsewell.exception;

public class RestrictedAccessException extends RuntimeException {
    public RestrictedAccessException(String message) {
        super(message);
    }

    public RestrictedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestrictedAccessException(Throwable cause) {
        super(cause);
    }

}

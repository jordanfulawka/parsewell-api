package com.jordanfulawka.parsewell.exception;

public class JobFetchException extends RuntimeException {

    public enum Reason {
        BLOCKED,
        NOT_FOUND,
        UNREACHABLE,
        NO_CONTENT
    }

    private final Reason reason;

    public JobFetchException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}

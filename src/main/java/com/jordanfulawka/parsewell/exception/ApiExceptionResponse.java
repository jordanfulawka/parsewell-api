package com.jordanfulawka.parsewell.exception;

public class ApiExceptionResponse {

    private int status;
    private String error;
    private long timestamp;

    public ApiExceptionResponse() {}

    public ApiExceptionResponse(int status, String error, long timestamp) {
        this.status = status;
        this.error = error;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiExceptionResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

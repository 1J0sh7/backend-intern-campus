package com.company.exception;

public class ApiErrorResponse {
    private String message;
    private int status;


//setters and getters

    public ApiErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
package com.example.positionbookservice.exception;

public class InvalidTradeEventException extends RuntimeException {
    public InvalidTradeEventException(String message) {
        super(message);
    }
}

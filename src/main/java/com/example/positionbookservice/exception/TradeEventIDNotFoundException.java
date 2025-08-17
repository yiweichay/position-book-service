package com.example.positionbookservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeEventIDNotFoundException extends RuntimeException {
    public TradeEventIDNotFoundException(String message) {
        super(message);
    }
}

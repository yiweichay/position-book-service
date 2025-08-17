package com.example.positionbookservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedEventIDBadRequestException extends RuntimeException {
    public DuplicatedEventIDBadRequestException(String message) {
        super(message);
    }
}

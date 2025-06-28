package com.igorsouza.games.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends Exception {
    private final HttpStatus status = HttpStatus.CONFLICT;

    public ConflictException(String message) {
        super(message);
    }
}

package com.serhiimanyk.backend.exception;

public class TimeslotNotFoundException extends RuntimeException {
    public TimeslotNotFoundException(String message) {
        super(message);
    }
}

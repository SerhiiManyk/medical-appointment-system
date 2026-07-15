package com.serhiimanyk.backend.exception;

public class TimeslotAlreadyBookedException extends RuntimeException {
    public TimeslotAlreadyBookedException(String message) {
        super(message);
    }
}

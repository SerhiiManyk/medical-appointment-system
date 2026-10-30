package com.serhiimanyk.backend.exception;

public class AppointmentAlreadyFinishedException extends RuntimeException {
    public AppointmentAlreadyFinishedException(String message) {
        super(message);
    }
}

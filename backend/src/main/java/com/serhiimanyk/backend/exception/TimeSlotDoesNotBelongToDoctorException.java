package com.serhiimanyk.backend.exception;

public class TimeSlotDoesNotBelongToDoctorException extends RuntimeException {
    public TimeSlotDoesNotBelongToDoctorException(String message) {
        super(message);
    }
}

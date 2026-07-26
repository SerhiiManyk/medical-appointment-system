package com.serhiimanyk.backend.handler;

import com.serhiimanyk.backend.dto.response.ErrorResponse;
import com.serhiimanyk.backend.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .code(status.value())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDoctorNotFoundException(DoctorNotFoundException exception,
                                                                       HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception,
                                                                           HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AppointmentAlreadyFinishedException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentAlreadyFinishedException(AppointmentAlreadyFinishedException exception,
                                                                                   HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentNotFoundException(AppointmentNotFoundException exception,
                                                                            HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidTimeSlotException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTimeSlotException(InvalidTimeSlotException exception,
                                                                            HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFoundException(PatientNotFoundException exception,
                                                                        HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TimeSlotAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleTimeslotAlreadyBookedException(TimeSlotAlreadyBookedException exception,
                                                                              HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(TimeSlotDoesNotBelongToDoctorException.class)
    public ResponseEntity<ErrorResponse> handleTimeSlotDoesNotBelongToDoctorException(TimeSlotDoesNotBelongToDoctorException exception,
                                                                              HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TimeSlotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTimeslotNotFoundException(TimeSlotNotFoundException exception,
                                                                         HttpServletRequest request) {

        ErrorResponse response = buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

package com.serhiimanyk.backend.handler;

import com.serhiimanyk.backend.dto.response.ErrorResponse;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDoctorNotFoundException(DoctorNotFoundException exception,
                                                                       HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND)
                .code(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

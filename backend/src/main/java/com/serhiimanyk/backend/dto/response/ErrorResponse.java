package com.serhiimanyk.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;

    private HttpStatus status;

    private Integer code;

    private String message;

    private String path;
}

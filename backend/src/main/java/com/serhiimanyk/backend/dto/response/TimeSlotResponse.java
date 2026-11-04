package com.serhiimanyk.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class TimeSlotResponse {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private TimeSlotStatus status;

    private Long doctorId;
}

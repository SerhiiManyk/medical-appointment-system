package com.serhiimanyk.backend.dto.response;

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

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private TimeSlotStatus status;

    private Long doctorId;
}

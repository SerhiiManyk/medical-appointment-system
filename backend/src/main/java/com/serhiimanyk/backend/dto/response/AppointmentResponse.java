package com.serhiimanyk.backend.dto.response;

import com.serhiimanyk.backend.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class AppointmentResponse {

    private Long id;

    private Long patientId;

    private Long doctorId;

    private Long timeSlotId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private AppointmentStatus status;
}

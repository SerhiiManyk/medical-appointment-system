package com.serhiimanyk.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.Specialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;

    private Long patientId;
    private String patientFirstName;
    private String patientLastName;

    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private Specialization specialization;

    private Long timeSlotId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private AppointmentStatus status;

    private String comment;
}

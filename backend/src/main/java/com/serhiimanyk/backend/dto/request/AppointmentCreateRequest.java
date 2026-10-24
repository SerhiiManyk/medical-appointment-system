package com.serhiimanyk.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentCreateRequest {

    @NotNull(message = "{NotNull.appointment.patient}")
    private Long patientId;

    @NotNull(message = "{NotNull.appointment.doctor}")
    private Long doctorId;

    @NotNull(message = "{NotNull.appointment.timeSlot}")
    private Long timeSlotId;
}

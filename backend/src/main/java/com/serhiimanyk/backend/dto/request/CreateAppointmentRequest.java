package com.serhiimanyk.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentRequest {

    private Long patientId;

    private Long doctorId;

    private Long timeSlotId;
}

package com.serhiimanyk.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientUpdateRequest extends PatientRequest {

    @Size(min = 6, max = 100, message = "{Size.patient.password}")
    private String password;

}

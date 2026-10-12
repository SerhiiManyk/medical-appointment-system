package com.serhiimanyk.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCreateRequest extends PatientRequest {

    @NotBlank
    @Size(min = 6, max = 100, message = "{Size.patient.password}")
    private String password;

}

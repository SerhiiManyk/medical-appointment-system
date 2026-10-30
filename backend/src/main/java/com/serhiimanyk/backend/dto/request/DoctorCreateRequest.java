package com.serhiimanyk.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorCreateRequest extends DoctorRequest{

    @NotBlank(message = "{NotBlank.doctor.password}")
    @Size(min = 6, max = 100, message = "{Size.doctor.password}")
    private String password;
}

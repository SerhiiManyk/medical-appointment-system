package com.serhiimanyk.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "{NotEmpty.patient.email}")
    @Email(message = "{Email.patient.email}")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "{NotEmpty.patient.password}")
    @Size(min = 6, max = 100, message = "{Size.patient.password}")
    private String password;
}

package com.serhiimanyk.backend.dto.request;

import com.serhiimanyk.backend.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRequest {

    @NotBlank(message = "{NotEmpty.patient_first.name}")
    @Size(max = 100, message = "{Size.patient_first.name}")
    private String firstName;

    @NotBlank(message = "{NotEmpty.patient.name}")
    @Size(max = 100, message = "{Size.patient.name}")
    private String lastName;

    @NotBlank(message = "{NotEmpty.patient.email}")
    @Email(message = "{Email.patient.email}")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "{NotEmpty.patient.password}")
    @Size(min = 6, max = 100, message = "{Size.patient.password}")
    private String password;

    @Pattern(
            regexp = "^\\+?[0-9]{7,20}$",
            message = "{Pattern.user.phone}"
    )
    private String phoneNumber;

    @NotNull(message = "{NotNull.patient.gender}")
    private Gender gender;

    @NotNull(message = "{NotNull.patient.dateOfBirth}")
    @Past(message = "{Past.patient.dateOfBirth}")
    private LocalDate dateOfBirth;
}

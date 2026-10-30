package com.serhiimanyk.backend.dto.request;

import com.serhiimanyk.backend.enums.Specialization;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRequest {

    @NotBlank(message = "{NotEmpty.doctor_first.name}")
    @Size(max = 100, message = "{Size.doctor_first.name}")
    private String firstName;

    @NotBlank(message = "{NotEmpty.doctor.name}")
    @Size(max = 100, message = "{Size.doctor.name}")
    private String lastName;

    @NotBlank(message = "{NotEmpty.doctor.email}")
    @Email(message = "{Email.doctor.email}")
    @Size(max = 100)
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9]{7,20}$",
            message = "{Pattern.user.phone}"
    )
    private String phoneNumber;

    @NotNull(message = "{NotNull.doctor.specialization}")
    private Specialization specialization;
}

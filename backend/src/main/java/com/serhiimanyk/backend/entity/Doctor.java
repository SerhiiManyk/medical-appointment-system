package com.serhiimanyk.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="DOCTORS")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCTOR_ID")
    private Long id;

    @NotBlank(message = "{NotEmpty.doctor_first.name}")
    @Size(max = 100, message = "{Size.doctor_first.name}")
    @Column(name = "DOCTOR_FIRST_NAME", length = 100, nullable = false)
    private String firstName;

    @NotBlank(message = "{NotEmpty.doctor.name}")
    @Size(max = 100, message = "{Size.doctor.name}")
    @Column(name = "DOCTOR_NAME", length = 100, nullable = false)
    private String lastName;

    @NotBlank(message = "{NotEmpty.doctor.email}")
    @Email(message = "{Email.doctor.email}")
    @Size(max = 100)
    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

    @NotBlank(message = "{NotEmpty.doctor.password}")
    @Size(min = 6, max = 100, message = "{Size.doctor.password}")
    @Column(name = "DOCTOR_PASSWORD", length = 100, nullable = false)
    private String password;

    @Pattern(
            regexp = "^\\+?[0-9]{7,20}$",
            message = "{Pattern.user.phone}"
    )
    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

}

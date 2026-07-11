package com.serhiimanyk.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="PATIENTS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PATIENT_ID")
    private Long id;

    @NotBlank(message = "{NotEmpty.patient_first.name}")
    @Size(max = 100, message = "{Size.patient_first.name}")
    @Column(name = "PATIENT_FIRST_NAME", length = 100, nullable = false)
    private String firstName;

    @NotBlank(message = "{NotEmpty.patient.name}")
    @Size(max = 100, message = "{Size.patient.name}")
    @Column(name = "PATIENT_NAME", length = 100, nullable = false)
    private String lastName;

    @NotBlank(message = "{NotEmpty.patient.email}")
    @Email(message = "{Email.patient.email}")
    @Size(max = 100)
    @Column(name = "PATIENT_EMAIL", length = 100, unique = true, nullable = false)
    private String email;

    @NotBlank(message = "{NotEmpty.patient.password}")
    @Size(min = 6, max = 100, message = "{Size.patient.password}")
    @Column(name = "PATIENT_PASSWORD", length = 100, nullable = false)
    private String password;

    @Pattern(
            regexp = "^\\+?[0-9]{7,20}$",
            message = "{Pattern.patient.phone}"
    )
    @Column(name = "PATIENT_PHONE_NUMBER", length = 20)
    private String phoneNumber;
}

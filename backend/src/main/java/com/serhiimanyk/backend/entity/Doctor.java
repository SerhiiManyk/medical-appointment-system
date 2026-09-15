package com.serhiimanyk.backend.entity;

import com.serhiimanyk.backend.enums.Specialization;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="DOCTORS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "DOCTOR_EMAIL", length = 100, unique = true, nullable = false)
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

    @NotNull(message = "{NotNull.doctor.specialization}")
    @Enumerated(EnumType.STRING)
    @Column(name = "DOCTOR_SPECIALIZATION", nullable = false)
    private Specialization specialization;

}

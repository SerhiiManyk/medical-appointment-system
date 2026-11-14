package com.serhiimanyk.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serhiimanyk.backend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class PatientResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private Gender gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
}

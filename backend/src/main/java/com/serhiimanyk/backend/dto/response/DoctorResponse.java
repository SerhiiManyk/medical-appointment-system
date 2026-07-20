package com.serhiimanyk.backend.dto.response;

import com.serhiimanyk.backend.enums.Specialization;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class DoctorResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private Specialization specialization;
}

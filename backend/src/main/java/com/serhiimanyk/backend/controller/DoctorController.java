package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @PostMapping
    public DoctorResponse createDoctor( @Valid @RequestBody DoctorRequest doctorRequest) {

        Doctor doctor = doctorMapper.toDoctor(doctorRequest);

        doctorService.createDoctor(doctor);

        return doctorMapper.toDoctorResponse(doctor);
    }
}

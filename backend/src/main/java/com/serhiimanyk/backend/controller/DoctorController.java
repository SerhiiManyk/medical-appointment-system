package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest doctorRequest) {

        Doctor doctor = doctorMapper.toDoctor(doctorRequest);

        Doctor resultDoctor = doctorService.createDoctor(doctor);

        DoctorResponse response = doctorMapper.toDoctorResponse(resultDoctor);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resultDoctor.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {

        List<Doctor> doctors = doctorService.getAllDoctors();

        List<DoctorResponse> resultList = doctorMapper.toDoctorsResponseList(doctors);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {

        Doctor doctor = doctorService.getDoctorById(id);

        DoctorResponse response = doctorMapper.toDoctorResponse(doctor);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@Valid @RequestBody DoctorRequest doctorRequest, @PathVariable Long id) {

        Doctor doctor = doctorService.updateDoctor(doctorRequest, id);

        DoctorResponse response = doctorMapper.toDoctorResponse(doctor);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable Long id) {

        doctorService.deleteDoctorById(id);

        return ResponseEntity.noContent().build();
    }
}

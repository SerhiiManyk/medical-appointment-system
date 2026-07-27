package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.PatientRequest;
import com.serhiimanyk.backend.dto.response.PatientResponse;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.mapper.PatientMapper;
import com.serhiimanyk.backend.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest patientRequest) {

        Patient patient = patientMapper.toPatient(patientRequest);

        Patient resultPatient = patientService.createPatient(patient);

        PatientResponse response = patientMapper.toPatientResponse(resultPatient);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resultPatient.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        List<Patient> patients = patientService.getAllPatients();

        List<PatientResponse> resultList = patientMapper.toPatientResponseList(patients);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {

        Patient patient = patientService.getPatientById(id);

        PatientResponse response = patientMapper.toPatientResponse(patient);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@Valid @RequestBody PatientRequest patientRequest, @PathVariable Long id) {

        Patient patient = patientService.updatePatient(patientRequest, id);

        PatientResponse response = patientMapper.toPatientResponse(patient);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientById(@PathVariable Long id) {

        patientService.deletePatientById(id);

        return ResponseEntity.noContent().build();
    }
}

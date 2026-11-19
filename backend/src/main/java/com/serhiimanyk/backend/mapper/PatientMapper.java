package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.PatientCreateRequest;
import com.serhiimanyk.backend.dto.request.PatientUpdateRequest;
import com.serhiimanyk.backend.dto.response.PatientResponse;
import com.serhiimanyk.backend.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientMapper {

    public PatientResponse toPatientResponse(Patient patient) {

        return PatientResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .build();
    }

    public List<PatientResponse> toPatientResponseList(
            List<Patient> patients
    ) {
        return patients.stream()
                .map(this::toPatientResponse)
                .toList();
    }

    public Patient toPatient(PatientCreateRequest patientRequest) {
        Patient patient = new Patient();
        patient.setFirstName(patientRequest.getFirstName());
        patient.setLastName(patientRequest.getLastName());
        patient.setGender(patientRequest.getGender());
        patient.setEmail(patientRequest.getEmail());
        patient.setPassword(patientRequest.getPassword());
        patient.setPhoneNumber(patientRequest.getPhoneNumber());
        patient.setDateOfBirth(patientRequest.getDateOfBirth());

        return patient;
    }

    public void updatePatientFromRequest(PatientUpdateRequest patientUpdateRequest, Patient patient) {
        patient.setFirstName(patientUpdateRequest.getFirstName());
        patient.setLastName(patientUpdateRequest.getLastName());
        patient.setGender(patientUpdateRequest.getGender());
        patient.setEmail(patientUpdateRequest.getEmail());
        patient.setPhoneNumber(patientUpdateRequest.getPhoneNumber());
        patient.setDateOfBirth(patientUpdateRequest.getDateOfBirth());
    }
}

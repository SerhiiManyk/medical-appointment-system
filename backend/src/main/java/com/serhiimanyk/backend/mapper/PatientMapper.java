package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.PatientRequest;
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

    public Patient toPatient(PatientRequest patientRequest) {
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

    public void updatePatientFromRequest(PatientRequest patientRequest, Patient patient) {
        patient.setFirstName(patientRequest.getFirstName());
        patient.setLastName(patientRequest.getLastName());
        patient.setGender(patientRequest.getGender());
        patient.setEmail(patientRequest.getEmail());
        patient.setPassword(patientRequest.getPassword());
        patient.setPhoneNumber(patientRequest.getPhoneNumber());
        patient.setDateOfBirth(patientRequest.getDateOfBirth());
    }
}

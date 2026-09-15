package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.PatientRequest;
import com.serhiimanyk.backend.entity.Patient;

import java.util.List;

public interface PatientService {

    Patient getPatientByEmail(String email);

    Patient getPatientById(Long id);

    List<Patient> getAllPatients ();

    Patient createPatient(Patient patient);

    Patient updatePatient(PatientRequest request, Long id);

    void deletePatientById(Long id);

}

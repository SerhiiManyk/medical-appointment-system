package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Patient;

import java.util.List;

public interface PatientService {

    Patient getPatientByEmail(String email);

    Patient getPatientById(Long id);

    List<Patient> getAllPatients ();

    Patient createPatient(Patient patient);

    Patient updatePatient(Patient patient);

    void deletePatientById(Long id);

    void checkEmailUnique(String email);
}

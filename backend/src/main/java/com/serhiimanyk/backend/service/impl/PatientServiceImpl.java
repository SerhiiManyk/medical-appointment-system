package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.exception.PatientNotFoundException;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email " + email + " is not found"));
    }

    @Override
    public Patient getPatientById(Long id) {

        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " is not found"));
    }

    @Override
    public List<Patient> getAllPatients() {

        return patientRepository.findAll();
    }

    @Override
    public Patient createPatient(Patient patient) {

        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new EmailAlreadyExistsException("Patient with email " + patient.getEmail() + " already exists");
        }
        return patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(Patient patient) {


        Patient patientToUpdate = getPatientById(patient.getId());

        patientToUpdate.setFirstName(patient.getFirstName());
        patientToUpdate.setLastName(patient.getLastName());
        patientToUpdate.setPassword(patient.getPassword());
        patientToUpdate.setPhoneNumber(patient.getPhoneNumber());
        patientToUpdate.setGender(patient.getGender());
        patientToUpdate.setDateOfBirth(patient.getDateOfBirth());

        if (!patientToUpdate.getEmail().equals(patient.getEmail())) {

            if (patientRepository.existsByEmailAndIdNot(
                    patient.getEmail(),
                    patient.getId()
            )) {
                throw new EmailAlreadyExistsException(
                        "Patient with email " + patient.getEmail() + " already exists"
                );
            }

            patientToUpdate.setEmail(patient.getEmail());
        }

        return patientRepository.save(patientToUpdate);
    }

    @Override
    public void deletePatientById(Long id) {

        Patient patient = getPatientById(id);

        patientRepository.delete(patient);
    }

}

package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.PatientRequest;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.exception.PatientNotFoundException;
import com.serhiimanyk.backend.mapper.PatientMapper;
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
    private final PatientMapper patientMapper;

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
    public Patient updatePatient(PatientRequest request, Long id) {

        Patient patientToUpdate = getPatientById(id);

        if (!patientToUpdate.getEmail().equals(request.getEmail())) {

            if (patientRepository.existsByEmailAndIdNot(
                    request.getEmail(),
                    id
            )) {
                throw new EmailAlreadyExistsException(
                        "Patient with email " + request.getEmail() + " already exists"
                );
            }

        }
        patientMapper.updatePatientFromRequest(request, patientToUpdate);

        return patientRepository.save(patientToUpdate);
    }

    @Override
    public void deletePatientById(Long id) {

        Patient patient = getPatientById(id);

        patientRepository.delete(patient);
    }

}

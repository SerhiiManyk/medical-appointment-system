package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.mapper.PatientMapper;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    private Patient patient;

    @InjectMocks
    PatientServiceImpl patientService;

    @Mock
    PatientRepository patientRepository;

    @Mock
    PatientMapper patientMapper;

    @BeforeEach
    public void setup() {
        patient = new Patient();
        patient.setId(1L);
        patient.setEmail("patient@test.com");
    }

    @Test
    public void getPatientByEmail_shouldReturnPatientSuccessfully() {
    }

    @Test
    public void getPatientByEmail_shouldThrowExceptionWhenPatientNotFound() {
    }

    @Test
    public void getPatientById_shouldReturnPatientSuccessfully() {
    }

    @Test
    public void getPatientById_shouldThrowExceptionWhenPatientNotFound() {
    }

    @Test
    public void getAllPatients_shouldReturnPatientsSuccessfully() {
    }

    @Test
    public void getAllPatients_shouldReturnEmptyListWhenNoPatientsFound() {
    }

    @Test
    public void createPatient_shouldCreatePatientSuccessfully() {
    }

    @Test
    public void createPatient_shouldThrowExceptionWhenEmailAlreadyExists() {
    }

    @Test
    public void updatePatient_shouldUpdatePatientSuccessfullyWhenEmailIsUnchanged() {
    }

    @Test
    public void updatePatient_shouldUpdatePatientSuccessfullyWhenEmailIsChanged() {
    }

    @Test
    public void updatePatient_shouldThrowExceptionWhenNewEmailAlreadyExists() {
    }

    @Test
    public void updatePatient_shouldThrowExceptionWhenPatientNotFound() {
    }

    @Test
    public void deletePatientById_shouldDeletePatientSuccessfully() {
    }

    @Test
    public void deletePatientById_shouldThrowExceptionWhenPatientNotFound() {
    }

}

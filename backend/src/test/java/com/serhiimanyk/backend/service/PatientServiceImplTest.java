package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.exception.PatientNotFoundException;
import com.serhiimanyk.backend.mapper.PatientMapper;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByEmail(patient.getEmail());

        assertEquals(patient, result);

        verify(patientRepository, times(1)).findByEmail(patient.getEmail());
    }

    @Test
    public void getPatientByEmail_shouldThrowExceptionWhenPatientNotFound() {

        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.empty());

        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> patientService.getPatientByEmail(patient.getEmail()));

        assertEquals("Patient with email " + patient.getEmail() + " is not found", exception.getMessage());

        verify(patientRepository, times(1)).findByEmail(patient.getEmail());
    }

    @Test
    public void getPatientById_shouldReturnPatientSuccessfully() {

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(patient.getId());

        assertEquals(patient, result);

        verify(patientRepository, times(1)).findById(patient.getId());
    }

    @Test
    public void getPatientById_shouldThrowExceptionWhenPatientNotFound() {

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> patientService.getPatientById(patient.getId()));

        assertEquals("Patient with id " + patient.getId() + " is not found", exception.getMessage());

        verify(patientRepository, times(1)).findById(patient.getId());
    }

    @Test
    public void getAllPatients_shouldReturnPatientsSuccessfully() {

        Patient patient1 = new Patient();
        patient1.setId(2L);

        List<Patient> patientList = List.of(patient1, patient);

        when(patientRepository.findAll()).thenReturn(patientList);

        List<Patient> result = patientService.getAllPatients();

        assertEquals(patientList, result);
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    public void getAllPatients_shouldReturnEmptyListWhenNoPatientsFound() {

        when(patientRepository.findAll()).thenReturn(List.of());

        List<Patient> result = patientService.getAllPatients();

        assertEquals(0, result.size());
        verify(patientRepository, times(1)).findAll();
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

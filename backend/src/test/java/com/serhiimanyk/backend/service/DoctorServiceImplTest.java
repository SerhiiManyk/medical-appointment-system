package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplTest {

    private Doctor doctor;

    @InjectMocks
    DoctorServiceImpl  doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @BeforeEach
    public void createTestDoctor() {

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("doctor@test.com");
    }

    @Test
    public void getDoctorByEmail_shouldReturnDoctorSuccessfully(){

        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        Doctor resultDoctor = doctorService.getDoctorByEmail(doctor.getEmail());

        assertEquals(resultDoctor, doctor);
        verify(doctorRepository, times(1)).findByEmail(doctor.getEmail());
    }

    @Test
    public void getDoctorByEmail_shouldThrowExceptionWhenDoctorNotFound(){

        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows( DoctorNotFoundException.class,
                () -> doctorService.getDoctorByEmail(doctor.getEmail()));

        assertEquals("Doctor with email " + doctor.getEmail() + " is not found", exception.getMessage());
        verify(doctorRepository, times(1)).findByEmail(doctor.getEmail());
    }

    @Test
    public void getDoctorById_shouldReturnDoctorSuccessfully(){}

    @Test
    public void getDoctorById_shouldThrowExceptionWhenDoctorNotFound(){}

    @Test
    public void getBySpecialization_shouldReturnDoctorsSuccessfully(){}

    @Test
    public void getBySpecialization_shouldReturnEmptyListWhenNoDoctorsFound(){}

    @Test
    public void getAllDoctors_shouldReturnDoctorsSuccessfully(){}

    @Test
    public void getAllDoctors_shouldReturnEmptyListWhenNoDoctorsFound(){}

    @Test
    public void createDoctor_shouldCreateDoctorSuccessfully(){}

    @Test
    public void createDoctor_shouldThrowExceptionWhenEmailAlreadyExists(){}

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfullyWhenEmailIsUnchanged(){}

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfullyWhenEmailIsChanged(){}

    @Test
    public void updateDoctor_shouldThrowExceptionWhenDoctorNotFound(){}

    @Test
    public void updateDoctor_shouldThrowExceptionWhenNewEmailAlreadyExists(){}

    @Test
    public void deleteDoctorById_shouldDeleteDoctorSuccessfully(){}

    @Test
    public void deleteDoctorById_shouldThrowExceptionWhenDoctorNotFound(){}


}

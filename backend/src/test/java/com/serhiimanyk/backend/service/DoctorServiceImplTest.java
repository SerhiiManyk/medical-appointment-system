package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.service.impl.DoctorServiceImpl;
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
public class DoctorServiceImplTest {

    private Doctor doctor;

    @InjectMocks
    DoctorServiceImpl doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @BeforeEach
    public void createTestDoctor() {

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("doctor@test.com");
        doctor.setSpecialization(Specialization.DENTIST);
    }

    @Test
    public void getDoctorByEmail_shouldReturnDoctorSuccessfully() {

        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        Doctor resultDoctor = doctorService.getDoctorByEmail(doctor.getEmail());

        assertEquals(resultDoctor, doctor);

        verify(doctorRepository, times(1)).findByEmail(doctor.getEmail());
    }

    @Test
    public void getDoctorByEmail_shouldThrowExceptionWhenDoctorNotFound() {

        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.getDoctorByEmail(doctor.getEmail()));

        assertEquals("Doctor with email " + doctor.getEmail() + " is not found", exception.getMessage());

        verify(doctorRepository, times(1)).findByEmail(doctor.getEmail());
    }

    @Test
    public void getDoctorById_shouldReturnDoctorSuccessfully() {

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));

        Doctor resultDoctor = doctorService.getDoctorById(doctor.getId());

        assertEquals(resultDoctor, doctor);

        verify(doctorRepository, times(1)).findById(doctor.getId());
    }

    @Test
    public void getDoctorById_shouldThrowExceptionWhenDoctorNotFound() {

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.getDoctorById(doctor.getId()));

        assertEquals("Doctor with id " + doctor.getId() + " not found", exception.getMessage());

        verify(doctorRepository, times(1)).findById(doctor.getId());
    }

    @Test
    public void getBySpecialization_shouldReturnDoctorsSuccessfully() {

        Doctor doctor2 = new Doctor();
        doctor2.setSpecialization(Specialization.DENTIST);

        List<Doctor> doctorList = List.of(doctor, doctor2);

        when(doctorRepository.findBySpecialization(doctor.getSpecialization())).thenReturn(doctorList);

        List<Doctor> resultDoctors = doctorService.getBySpecialization(doctor.getSpecialization());

        assertEquals(doctorList, resultDoctors);

        verify(doctorRepository, times(1)).findBySpecialization(doctor.getSpecialization());
    }

    @Test
    public void getBySpecialization_shouldReturnEmptyListWhenNoDoctorsFound() {

        List<Doctor> doctorList = List.of();

        when(doctorRepository.findBySpecialization(doctor.getSpecialization())).thenReturn(doctorList);

        List<Doctor> resultDoctors = doctorService.getBySpecialization(doctor.getSpecialization());

        assertEquals(doctorList, resultDoctors);

        verify(doctorRepository, times(1)).findBySpecialization(doctor.getSpecialization());
    }

    @Test
    public void getAllDoctors_shouldReturnDoctorsSuccessfully() {

        Doctor doctor2 = new Doctor();

        List<Doctor> doctorList = List.of(doctor, doctor2);

        when(doctorRepository.findAll()).thenReturn(doctorList);

        List<Doctor> resultDoctors = doctorService.getAllDoctors();

        assertEquals(doctorList, resultDoctors);

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    public void getAllDoctors_shouldReturnEmptyListWhenNoDoctorsFound() {

        List<Doctor> doctorList = List.of();

        when(doctorRepository.findAll()).thenReturn(doctorList);

        List<Doctor> resultDoctors = doctorService.getAllDoctors();

        assertEquals(doctorList, resultDoctors);

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    public void createDoctor_shouldCreateDoctorSuccessfully() {

        when(doctorRepository.existsByEmail(doctor.getEmail())).thenReturn(false);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor createdDoctor = doctorService.createDoctor(doctor);

        assertEquals(doctor, createdDoctor);

        verify(doctorRepository, times(1)).existsByEmail(doctor.getEmail());
        verify(doctorRepository, times(1)).save(doctor);

    }

    @Test
    public void createDoctor_shouldThrowExceptionWhenEmailAlreadyExists() {

        when(doctorRepository.existsByEmail(doctor.getEmail())).thenReturn(true);

        EmailAlreadyExistsException exception =  assertThrows(EmailAlreadyExistsException.class,
                () -> doctorService.createDoctor(doctor));

        assertEquals("Doctor with email " + doctor.getEmail() + " already exists", exception.getMessage());
        verify(doctorRepository, times(1)).existsByEmail(doctor.getEmail());
        verify(doctorRepository,never()).save(any());
    }

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfullyWhenEmailIsUnchanged() {

        DoctorRequest updateDoctor = new DoctorRequest();
        updateDoctor.setEmail("doctor@test.com");
        updateDoctor.setSpecialization(Specialization.CARDIOLOGIST);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor result = doctorService.updateDoctor(updateDoctor, 1L);

        assertEquals(doctor, result);

        verify(doctorRepository,never()).existsByEmailAndIdNot(doctor.getEmail(), 1L);
        verify(doctorMapper,times(1)).updateDoctorFromRequest(updateDoctor, doctor);
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfullyWhenEmailIsChanged() {

        DoctorRequest updateDoctor = new DoctorRequest();
        updateDoctor.setEmail("cardiologist@test.com");
        updateDoctor.setSpecialization(Specialization.CARDIOLOGIST);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByEmailAndIdNot(updateDoctor.getEmail(), 1L)).thenReturn(false);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor result = doctorService.updateDoctor(updateDoctor, 1L);

        assertEquals(doctor, result);

        verify(doctorMapper,times(1)).updateDoctorFromRequest(updateDoctor, doctor);
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).save(doctor);
        verify(doctorRepository, times(1)).existsByEmailAndIdNot(updateDoctor.getEmail(), 1L);
    }

    @Test
    public void updateDoctor_shouldThrowExceptionWhenDoctorNotFound() {

        DoctorRequest updateDoctor = new DoctorRequest();
        updateDoctor.setEmail("doctor@test.com");
        updateDoctor.setSpecialization(Specialization.CARDIOLOGIST);

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.updateDoctor(updateDoctor, 1L));

        assertEquals("Doctor with id 1 not found", exception.getMessage());
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, never()).save(any());
        verify(doctorMapper,never()).updateDoctorFromRequest(any(), any());
    }

    @Test
    public void updateDoctor_shouldThrowExceptionWhenNewEmailAlreadyExists() {

        DoctorRequest updateDoctor = new DoctorRequest();
        updateDoctor.setEmail("cardiologist@test.com");
        updateDoctor.setSpecialization(Specialization.CARDIOLOGIST);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByEmailAndIdNot(updateDoctor.getEmail(), 1L)).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> doctorService.updateDoctor(updateDoctor, 1L));

        assertEquals("Doctor with email " + updateDoctor.getEmail() + " already exists", exception.getMessage());

        verify(doctorMapper,never()).updateDoctorFromRequest(updateDoctor, doctor);
        verify(doctorRepository,times(1)).findById(1L);
        verify(doctorRepository,times(1)).existsByEmailAndIdNot(updateDoctor.getEmail(), 1L);
        verify(doctorRepository,never()).save(doctor);
    }

    @Test
    public void deleteDoctorById_shouldDeleteDoctorSuccessfully() {
    }

    @Test
    public void deleteDoctorById_shouldThrowExceptionWhenDoctorNotFound() {
    }


}

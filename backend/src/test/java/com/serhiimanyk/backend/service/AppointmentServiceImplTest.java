package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.repository.*;
import com.serhiimanyk.backend.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    private Patient patient;
    private Doctor doctor;
    private TimeSlot timeSlot;
    private AppointmentCreateRequest request;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private AppointmentRepository appointmentRepository;


    @InjectMocks
    private AppointmentServiceImpl appointmentService;


    @BeforeEach
    public void createTestPatient() {

        patient = new Patient();
        patient.setId(1L);
    }

    @BeforeEach
    public void createTestDoctor() {

        doctor = new Doctor();
        doctor.setId(1L);
    }

    @BeforeEach
    public void createTestTimeSlot() {
        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStatus(TimeSlotStatus.FREE);
        timeSlot.setDoctor(doctor);
    }

    @Test
    public void createAppointment_shouldCreateAppointmentSuccessfully() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));

        Appointment expectedAppointment = new Appointment();
        expectedAppointment.setPatient(patient);
        expectedAppointment.setDoctor(doctor);
        expectedAppointment.setTimeSlot(timeSlot);
        expectedAppointment.setStatus(AppointmentStatus.CREATED);

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(expectedAppointment);

        Appointment actualAppointment = appointmentService.createAppointment(request);

        assertEquals(expectedAppointment.getPatient(), actualAppointment.getPatient());
        assertEquals(expectedAppointment.getDoctor(), actualAppointment.getDoctor());
        assertEquals(expectedAppointment.getTimeSlot(), actualAppointment.getTimeSlot());
        assertEquals(expectedAppointment.getStatus(), actualAppointment.getStatus());
        assertEquals(TimeSlotStatus.BOOKED,actualAppointment.getTimeSlot().getStatus());

        verify(patientRepository).findById(patient.getId());
        verify(doctorRepository).findById(doctor.getId());
        verify(timeSlotRepository).findById(timeSlot.getId());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    public void createAppointment_shouldThrowException_whenPatientNotFound() {

    }

    @Test
    public void createAppointment_shouldThrowException_whenDoctorNotFound() {

    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotNotFound() {

    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotIsNotFree() {

    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotDoesNotBelongToDoctor() {

    }


}

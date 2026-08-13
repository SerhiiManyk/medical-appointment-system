package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.*;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation ->  invocation.getArgument(0));

        Appointment actualAppointment = appointmentService.createAppointment(request);

        assertEquals(expectedAppointment.getPatient(), actualAppointment.getPatient());
        assertEquals(expectedAppointment.getDoctor(), actualAppointment.getDoctor());
        assertEquals(expectedAppointment.getTimeSlot(), actualAppointment.getTimeSlot());
        assertEquals(expectedAppointment.getStatus(), actualAppointment.getStatus());
        assertEquals(TimeSlotStatus.BOOKED, actualAppointment.getTimeSlot().getStatus());

        verify(patientRepository).findById(patient.getId());
        verify(doctorRepository).findById(doctor.getId());
        verify(timeSlotRepository).findById(timeSlot.getId());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    public void createAppointment_shouldThrowException_whenPatientNotFound() {

        request = new AppointmentCreateRequest();
        request.setPatientId(10000L);
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientRepository.findById(10000L)).thenReturn(Optional.empty());

        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("Patient with id 10000 is not found", exception.getMessage());
        verify(patientRepository, times(1)).findById(10000L);
        verifyNoInteractions(doctorRepository, timeSlotRepository, appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenDoctorNotFound() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(10000L);
        request.setTimeSlotId(timeSlot.getId());

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(10000L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(
                DoctorNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("Doctor with id 10000 is not found", exception.getMessage());
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(doctorRepository, times(1)).findById(10000L);
        verifyNoInteractions(timeSlotRepository, appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotNotFound() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(10000L);

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findById(10000L)).thenReturn(Optional.empty());

        TimeSlotNotFoundException exception = assertThrows(
                TimeSlotNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("Timeslot with id 10000 is not found", exception.getMessage());
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(timeSlotRepository, times(1)).findById(10000L);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotIsNotFree() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
        timeSlot.setStatus(TimeSlotStatus.BOOKED);

        InvalidTimeSlotException exception = assertThrows(
                InvalidTimeSlotException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("TimeSlot is not available.", exception.getMessage());
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(timeSlotRepository, times(1)).findById(timeSlot.getId());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotDoesNotBelongToDoctor() {

        Doctor testDoctor = new Doctor();
        testDoctor.setId(10000L);

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
        timeSlot.setDoctor(testDoctor);

        TimeSlotDoesNotBelongToDoctorException exception = assertThrows(
                TimeSlotDoesNotBelongToDoctorException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("TimeSlot with id 1 does not belong to doctor with id 1", exception.getMessage());
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(timeSlotRepository, times(1)).findById(timeSlot.getId());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    public void getAppointmentById_shouldReturnAppointmentSuccessfully(){

        Appointment testAppointment = new Appointment();
        testAppointment.setPatient(patient);
        testAppointment.setDoctor(doctor);
        testAppointment.setTimeSlot(timeSlot);
        testAppointment.setStatus(AppointmentStatus.CREATED);

        when (appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        Appointment appointment = appointmentService.getAppointmentById(1L);

        assertEquals(testAppointment, appointment);

        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    public void getAppointmentById_shouldThrowException_whenAppointmentNotFound() {

        when (appointmentRepository.findById(10000L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception =  assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.getAppointmentById(10000L));

        assertEquals("Appointment not found", exception.getMessage());

        verify(appointmentRepository, times(1)).findById(10000L);
    }

    @Test
    public void getAppointmentsByPatientId_shouldReturnAppointmentsSuccessfully() {

    }

    @Test
    public void getAppointmentsByPatientId_shouldReturnEmptyList_whenPatientHasNoAppointments() {

    }

    @Test
    public void getAppointmentsByPatientId_shouldThrowException_whenPatientNotFound(){

    }

    @Test
    public void getAppointmentsByDoctorId_shouldReturnAppointmentsSuccessfully(){

    }

    @Test
    public void getAppointmentsByDoctorId_shouldReturnEmptyList_whenDoctorHasNoAppointments(){

    }

    @Test
    public void getAppointmentsByDoctorId_shouldThrowException_whenDoctorNotFound(){

    }

    @Test
    public void getAllAppointments_shouldReturnAppointmentsSuccessfully(){

    }

    @Test
    public void getAllAppointments_shouldReturnEmptyList_whenNoAppointmentsExist(){

    }

    @Test
    public void deleteAppointmentById_shouldDeleteAppointmentSuccessfully(){

    }

    @Test
    public void deleteAppointmentById_shouldThrowException_whenAppointmentNotFound(){

    }

    @Test
    public void cancelAppointment_shouldCancelAppointmentSuccessfully(){

    }

    @Test
    public void cancelAppointmentById_shouldThrowException_whenAppointmentNotFound(){

    }

    @Test
    public void cancelAppointment_shouldThrowException_whenAppointmentAlreadyCancelled(){

    }

    @Test
    public void cancelAppointment_shouldThrowException_whenAppointmentAlreadyCompleted(){

    }

    @Test
    public void completeAppointment_shouldCompleteAppointmentSuccessfully(){

    }

    @Test
    public void completeAppointment_shouldThrowException_whenAppointmentNotFound(){

    }

    @Test
    public void completeAppointment_shouldThrowException_whenAppointmentAlreadyCompleted(){

    }

    @Test
    public void completeAppointment_shouldThrowException_whenAppointmentAlreadyCancelled(){

    }

    @Test
    public void rescheduleAppointment_shouldRescheduleAppointmentSuccessfully(){

    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenAppointmentNotFound(){

    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotNotFound(){

    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotIsNotFree(){

    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotBelongsToAnotherDoctor(){

    }
}

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

import java.util.List;
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

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
    public void getAppointmentById_shouldReturnAppointmentSuccessfully() {

        Appointment testAppointment = new Appointment();
        testAppointment.setPatient(patient);
        testAppointment.setDoctor(doctor);
        testAppointment.setTimeSlot(timeSlot);
        testAppointment.setStatus(AppointmentStatus.CREATED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        Appointment appointment = appointmentService.getAppointmentById(1L);

        assertEquals(testAppointment, appointment);

        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    public void getAppointmentById_shouldThrowException_whenAppointmentNotFound() {

        when(appointmentRepository.findById(10000L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.getAppointmentById(10000L));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(10000L);
    }

    @Test
    public void getAppointmentsByPatientId_shouldReturnAppointmentsSuccessfully() {

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Appointment appointment1 = new Appointment();
        appointment1.setPatient(patient);

        Appointment appointment2 = new Appointment();
        appointment2.setPatient(patient);

        List<Appointment> appointmentList = List.of(appointment1, appointment2);

        when(appointmentRepository.findByPatientId(1L)).thenReturn(appointmentList);

        List<Appointment> resultList = appointmentService.getAppointmentsByPatientId(1L);

        assertEquals(appointmentList, resultList);
        verify(appointmentRepository, times(1)).findByPatientId(1L);
        verify(patientRepository, times(1)).findById(1L);

    }

    @Test
    public void getAppointmentsByPatientId_shouldReturnEmptyList_whenPatientHasNoAppointments() {

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        List<Appointment> appointmentList = List.of();

        when(appointmentRepository.findByPatientId(1L)).thenReturn(appointmentList);

        List<Appointment> resultList = appointmentService.getAppointmentsByPatientId(1L);

        assertEquals(appointmentList, resultList);
        verify(appointmentRepository, times(1)).findByPatientId(1L);
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    public void getAppointmentsByPatientId_shouldThrowException_whenPatientNotFound() {

        when(patientRepository.findById(10000L)).thenReturn(Optional.empty());

        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> appointmentService.getAppointmentsByPatientId(10000L)
        );

        assertEquals("Patient with id 10000 is not found", exception.getMessage());
        verify(patientRepository, times(1)).findById(10000L);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    public void getAppointmentsByDoctorId_shouldReturnAppointmentsSuccessfully() {

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Appointment appointment1 = new Appointment();
        appointment1.setDoctor(doctor);

        Appointment appointment2 = new Appointment();
        appointment2.setDoctor(doctor);

        List<Appointment> appointmentList = List.of(appointment1, appointment2);

        when(appointmentRepository.findByDoctorId(1L)).thenReturn(appointmentList);

        List<Appointment> resultList = appointmentService.getAppointmentsByDoctorId(1L);

        assertEquals(appointmentList, resultList);
        verify(appointmentRepository, times(1)).findByDoctorId(1L);
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    public void getAppointmentsByDoctorId_shouldReturnEmptyList_whenDoctorHasNoAppointments() {

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        List<Appointment> appointmentList = List.of();

        when(appointmentRepository.findByDoctorId(1L)).thenReturn(appointmentList);

        List<Appointment> resultList = appointmentService.getAppointmentsByDoctorId(1L);

        assertEquals(appointmentList, resultList);
        verify(appointmentRepository, times(1)).findByDoctorId(1L);
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    public void getAppointmentsByDoctorId_shouldThrowException_whenDoctorNotFound() {

        when(doctorRepository.findById(10000L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> appointmentService.getAppointmentsByDoctorId(10000L));

        assertEquals("Doctor with id 10000 is not found", exception.getMessage());
        verify(doctorRepository, times(1)).findById(10000L);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    public void getAllAppointments_shouldReturnAppointmentsSuccessfully() {

        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();

        List<Appointment> appointmentList = List.of(appointment1, appointment2);

        when(appointmentRepository.findAll()).thenReturn(appointmentList);

        List<Appointment> resultList = appointmentService.getAllAppointments();

        assertEquals(appointmentList, resultList);
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    public void getAllAppointments_shouldReturnEmptyList_whenNoAppointmentsExist() {

        List<Appointment> appointmentList = List.of();

        when(appointmentRepository.findAll()).thenReturn(appointmentList);

        List<Appointment> resultList = appointmentService.getAllAppointments();

        assertEquals(appointmentList, resultList);
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    public void deleteAppointmentById_shouldDeleteAppointmentSuccessfully() {

        appointmentService.deleteAppointmentById(1L);

        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void cancelAppointment_shouldCancelAppointmentSuccessfully() {

        Appointment appointment = new Appointment();
        TimeSlot timeSlot = new TimeSlot();
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CREATED);
        timeSlot.setStatus(TimeSlotStatus.BOOKED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.cancelAppointment(1L);

        assertEquals(appointment, result);
        assertEquals(TimeSlotStatus.FREE, result.getTimeSlot().getStatus());
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    public void cancelAppointment_shouldThrowException_whenAppointmentNotFound() {

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.cancelAppointment(1L));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void cancelAppointment_shouldThrowException_whenAppointmentAlreadyCancelled() {

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.cancelAppointment(1L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void cancelAppointment_shouldThrowException_whenAppointmentAlreadyCompleted() {

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.cancelAppointment(1L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));

    }

    @Test
    public void completeAppointment_shouldCompleteAppointmentSuccessfully() {

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CREATED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.completeAppointment(1L);

        assertEquals(appointment, result);
        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    public void completeAppointment_shouldThrowException_whenAppointmentNotFound() {

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.completeAppointment(1L));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void completeAppointment_shouldThrowException_whenAppointmentAlreadyCompleted() {

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.completeAppointment(1L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void completeAppointment_shouldThrowException_whenAppointmentAlreadyCancelled() {

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.completeAppointment(1L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_shouldRescheduleAppointmentSuccessfully() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        appointment.setDoctor(doctor);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setId(1L);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(timeSlotRepository.findById(2L)).thenReturn(Optional.of(newTimeSlot));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.rescheduleAppointment(1L, 2L);

        assertEquals(TimeSlotStatus.BOOKED, newTimeSlot.getStatus());
        assertEquals(TimeSlotStatus.FREE, oldTimeSlot.getStatus());
        assertEquals(newTimeSlot, result.getTimeSlot());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findById(2L);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenAppointmentNotFound() {

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.rescheduleAppointment(1L,2L));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenAppointmentAlreadyCancelled() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenAppointmentAlreadyCompleted() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotNotFound() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findById(2L)).thenReturn(Optional.empty());

        TimeSlotNotFoundException exception = assertThrows(TimeSlotNotFoundException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("Time slot with id 2 not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findById(2L);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotIsNotFree() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.BOOKED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findById(2L)).thenReturn(Optional.of(newTimeSlot));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("TimeSlot is not available.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findById(2L);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotBelongsToAnotherDoctor() {

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setDoctor(doctor1);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor2);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findById(2L)).thenReturn(Optional.of(newTimeSlot));

        TimeSlotDoesNotBelongToDoctorException exception = assertThrows(TimeSlotDoesNotBelongToDoctorException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("TimeSlot with id 2 does not belong to doctor with id 1", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findById(2L);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotIsAlreadyCurrentTimeSlot() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        appointment.setDoctor(doctor);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setId(1L);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(1L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(newTimeSlot));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> appointmentService.rescheduleAppointment(1L, 1L));
        assertEquals("Timeslot with id 1 is already in progress.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findById(1L);
    }
}

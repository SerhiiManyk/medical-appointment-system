package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.Specialization;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Mock
    private PatientService patientService;


    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Mock
    private DoctorService doctorService;

    @BeforeEach
    public void createTestPatient() {

        patient = new Patient();
        patient.setId(1L);
        patient.setEmail("test@email.com");
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

    @BeforeEach
    public void authenticationDoctor() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );
    }

    @Test
    public void createAppointment_shouldCreateAppointmentSuccessfully() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientService.getPatientFromAuthentication()).thenReturn(patient);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findTimeSlotByIdWithLock(timeSlot.getId())).thenReturn(Optional.of(timeSlot));

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

        verify(patientService).getPatientFromAuthentication();
        verify(doctorRepository).findById(doctor.getId());
        verify(timeSlotRepository).findTimeSlotByIdWithLock(timeSlot.getId());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    public void createAppointment_shouldThrowException_whenPatientNotFound() {

        request = new AppointmentCreateRequest();
        request.setPatientId(10000L);
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientService.getPatientFromAuthentication()).thenThrow(new UsernameNotFoundException(
                "Patient with email test@email.com not found"
        ));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals(
                "Patient with email test@email.com not found",
                exception.getMessage()
        );
        verifyNoInteractions(doctorRepository, timeSlotRepository, appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenDoctorNotFound() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(10000L);
        request.setTimeSlotId(timeSlot.getId());

        when(patientService.getPatientFromAuthentication()).thenReturn(patient);
        when(doctorRepository.findById(10000L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(
                DoctorNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("Doctor with id 10000 is not found", exception.getMessage());
        verify(patientService, times(1)).getPatientFromAuthentication();
        verify(doctorRepository, times(1)).findById(10000L);
        verifyNoInteractions(timeSlotRepository, appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotNotFound() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(10000L);

        when(timeSlotRepository.findTimeSlotByIdWithLock(10000L)).thenReturn(Optional.empty());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(patientService.getPatientFromAuthentication()).thenReturn(patient);

        TimeSlotNotFoundException exception = assertThrows(
                TimeSlotNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("Timeslot with id 10000 is not found", exception.getMessage());
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(10000L);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    public void createAppointment_shouldThrowException_whenTimeSlotIsNotFree() {

        request = new AppointmentCreateRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setTimeSlotId(timeSlot.getId());

        when(patientService.getPatientFromAuthentication()).thenReturn(patient);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findTimeSlotByIdWithLock(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
        timeSlot.setStatus(TimeSlotStatus.BOOKED);

        InvalidTimeSlotException exception = assertThrows(
                InvalidTimeSlotException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("TimeSlot is not available.", exception.getMessage());
        verify(patientService, times(1)).getPatientFromAuthentication();
        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(timeSlot.getId());
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

        when(patientService.getPatientFromAuthentication()).thenReturn(patient);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findTimeSlotByIdWithLock(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
        timeSlot.setDoctor(testDoctor);

        TimeSlotDoesNotBelongToDoctorException exception = assertThrows(
                TimeSlotDoesNotBelongToDoctorException.class,
                () -> appointmentService.createAppointment(request)
        );

        assertEquals("TimeSlot with id 1 does not belong to doctor with id 1", exception.getMessage());
        verify(patientService, times(1)).getPatientFromAuthentication();
        verify(doctorRepository, times(1)).findById(doctor.getId());
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(timeSlot.getId());
        verifyNoInteractions(appointmentRepository);
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

        when(doctorService.isCurrentDoctor(1L)).thenReturn(true);

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

        when(doctorService.isCurrentDoctor(1L)).thenReturn(true);

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
    public void deleteAppointmentById_shouldDeleteAppointmentSuccessfully() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointmentById(1L);

        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void cancelAppointment_shouldCancelAppointmentSuccessfully() {

        Appointment appointment = new Appointment();
        TimeSlot timeSlot = new TimeSlot();
        appointment.setTimeSlot(timeSlot);
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.CREATED);
        timeSlot.setStatus(TimeSlotStatus.BOOKED);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.CREATED);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyFinishedException exception = assertThrows(AppointmentAlreadyFinishedException.class,
                () -> appointmentService.completeAppointment(1L));

        assertEquals("Appointment is already cancelled or completed.", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_shouldRescheduleAppointmentSuccessfully() {

        doctor.setSpecialization(Specialization.FAMILY_DOCTOR);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setId(1L);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(timeSlotRepository.findTimeSlotByIdWithLock(2L)).thenReturn(Optional.of(newTimeSlot));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.rescheduleAppointment(1L, 2L);

        assertEquals(TimeSlotStatus.BOOKED, newTimeSlot.getStatus());
        assertEquals(TimeSlotStatus.FREE, oldTimeSlot.getStatus());
        assertEquals(newTimeSlot, result.getTimeSlot());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(2L);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenAppointmentNotFound() {

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenAppointmentAlreadyCancelled() {

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setId(1L);
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setDoctor(doctor1);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setTimeSlot(oldTimeSlot);
        appointment.setDoctor(doctor1);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setId(1L);
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setDoctor(doctor1);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setTimeSlot(oldTimeSlot);
        appointment.setDoctor(doctor1);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

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

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setId(1L);
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setDoctor(doctor1);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setTimeSlot(oldTimeSlot);
        appointment.setDoctor(doctor1);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findTimeSlotByIdWithLock(2L)).thenReturn(Optional.empty());

        TimeSlotNotFoundException exception = assertThrows(TimeSlotNotFoundException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("Time slot with id 2 not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(2L);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotIsNotFree() {

        doctor.setSpecialization(Specialization.FAMILY_DOCTOR);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        newTimeSlot.setDoctor(doctor);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setId(1L);
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findTimeSlotByIdWithLock(2L)).thenReturn(Optional.of(newTimeSlot));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("TimeSlot is not available.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(2L);
    }

    @Test
    public void rescheduleAppointment_shouldRescheduleAppointmentSuccessfully_whenNewTimeSlotBelongsToAnotherDoctorWithTheSameSpecialization() {

        doctor.setSpecialization(Specialization.FAMILY_DOCTOR);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setSpecialization(Specialization.FAMILY_DOCTOR);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor2);
        newTimeSlot.setDate(LocalDate.of(2026, 11, 10));
        newTimeSlot.setStartTime(LocalTime.of(10, 0));
        newTimeSlot.setEndTime(LocalTime.of(10, 30));

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setId(1L);
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findTimeSlotByIdWithLock(2L)).thenReturn(Optional.of(newTimeSlot));

        when(appointmentRepository.existsByPatient_IdAndTimeSlot_DateAndTimeSlot_StartTimeAndTimeSlot_EndTimeAndStatusAndIdNot(
                1L,
                newTimeSlot.getDate(),
                newTimeSlot.getStartTime(),
                newTimeSlot.getEndTime(),
                AppointmentStatus.CREATED,
                1L))
                .thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.rescheduleAppointment(1L, 2L);

        assertEquals(TimeSlotStatus.BOOKED, newTimeSlot.getStatus());
        assertEquals(TimeSlotStatus.FREE, oldTimeSlot.getStatus());
        assertEquals(newTimeSlot, result.getTimeSlot());

        assertEquals(doctor2, result.getDoctor());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(2L);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotIsAlreadyCurrentTimeSlot() {

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        appointment.setDoctor(doctor);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setId(1L);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(1L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findTimeSlotByIdWithLock(1L)).thenReturn(Optional.of(newTimeSlot));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> appointmentService.rescheduleAppointment(1L, 1L));
        assertEquals("Timeslot with id 1 is already in progress.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(1L);
    }

    @Test
    public void rescheduleAppointment_shouldThrowException_whenNewTimeSlotBelongsToDoctorWithOtherSpecialization() {

        doctor.setSpecialization(Specialization.FAMILY_DOCTOR);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setSpecialization(Specialization.DERMATOLOGIST);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setDoctor(doctor);

        TimeSlot newTimeSlot = new TimeSlot();
        newTimeSlot.setId(2L);
        newTimeSlot.setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setDoctor(doctor2);

        TimeSlot oldTimeSlot = new TimeSlot();
        oldTimeSlot.setId(1L);
        oldTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        oldTimeSlot.setDoctor(doctor);

        appointment.setTimeSlot(oldTimeSlot);

        when(doctorService.getDoctorFromAuthentication()).thenReturn(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(timeSlotRepository.findTimeSlotByIdWithLock(2L)).thenReturn(Optional.of(newTimeSlot));

        TimeSlotDoesNotBelongToDoctorException exception = assertThrows(TimeSlotDoesNotBelongToDoctorException.class,
                () -> appointmentService.rescheduleAppointment(1L, 2L));

        assertEquals("Cannot reschedule appointment to a doctor with a different specialization.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findTimeSlotByIdWithLock(2L);
    }
}

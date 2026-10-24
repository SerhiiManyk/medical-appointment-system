package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.exception.InvalidTimeSlotException;
import com.serhiimanyk.backend.exception.TimeSlotDoesNotBelongToDoctorException;
import com.serhiimanyk.backend.exception.TimeSlotNotFoundException;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.impl.TimeslotServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeslotServiceImplTest {

    private Doctor doctor;
    private TimeSlot timeSlot;

    @InjectMocks
    TimeslotServiceImpl timeslotService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @BeforeEach
    public void createTestDoctor() {

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("doctor@test.mail");
    }

    @BeforeEach
    public void createTestTimeSlot() {
        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStatus(TimeSlotStatus.FREE);
        timeSlot.setDoctor(doctor);
    }

    @AfterEach
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createTimeSlot_shouldCreateTimeSlotSuccessfully() {

        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStatus(TimeSlotStatus.FREE);
        timeSlot.setDoctor(doctor);
        timeSlot.setStartTime(LocalTime.of(10, 30));
        timeSlot.setEndTime(LocalTime.of(11, 0));
        timeSlot.setDate(LocalDate.of(2026, 12, 30));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        when(timeSlotRepository.existsByDoctorIdAndDateAndStartTimeAndEndTime(1L, timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime()))
                .thenReturn(false);

        when(timeSlotRepository.save(timeSlot)).thenReturn(timeSlot);

        TimeSlot result = timeslotService.createTimeSlot(1L, timeSlot);

        assertEquals(timeSlot, result);
        assertEquals(doctor, result.getDoctor());
        assertEquals(TimeSlotStatus.FREE, result.getStatus());

        verify(doctorRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).existsByDoctorIdAndDateAndStartTimeAndEndTime(1L, timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime());
        verify(timeSlotRepository, times(1)).save(timeSlot);
    }

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {

        timeSlot = new TimeSlot();
        timeSlot.setStartTime(LocalTime.of(12, 30));
        timeSlot.setEndTime(LocalTime.of(11, 0));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.createTimeSlot(1L, timeSlot));

        assertEquals("Time slot end time is before time slot start time.", exception.getMessage());
        verifyNoInteractions(timeSlotRepository, doctorRepository);
    }

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenDateIsInThePast() {

        timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2025, 12, 30));
        timeSlot.setStartTime(LocalTime.of(12, 30));
        timeSlot.setEndTime(LocalTime.of(13, 0));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.createTimeSlot(1L, timeSlot));

        assertEquals("Time slot date is before time slot start date.", exception.getMessage());
        verifyNoInteractions(timeSlotRepository, doctorRepository);
    }

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenDoctorNotFound() {

        timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2055, 12, 30));
        timeSlot.setStartTime(LocalTime.of(12, 30));
        timeSlot.setEndTime(LocalTime.of(13, 0));

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> timeslotService.createTimeSlot(1L, timeSlot));

        assertEquals("Doctor not found.", exception.getMessage());
        verify(doctorRepository, times(1)).findById(1L);
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenTimeSlotAlreadyExists() {

        timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2055, 12, 30));
        timeSlot.setStartTime(LocalTime.of(12, 30));
        timeSlot.setEndTime(LocalTime.of(13, 0));
        timeSlot.setDoctor(doctor);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.existsByDoctorIdAndDateAndStartTimeAndEndTime(doctor.getId(), timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime()))
                .thenReturn(true);

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.createTimeSlot(1L, timeSlot));

        assertEquals("Time slot already exists.", exception.getMessage());
        verify(doctorRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1))
                .existsByDoctorIdAndDateAndStartTimeAndEndTime(doctor.getId(), timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime());
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    public void getTimeSlotById_shouldReturnTimeSlotSuccessfully() {

        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDoctor(doctor);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        TimeSlot result = timeslotService.getTimeSlotById(doctor.getId(), timeSlot.getId());

        assertEquals(timeSlot, result);
        assertEquals(timeSlot.getDoctor(), result.getDoctor());

        verify(timeSlotRepository, times(1)).findById(1L);
    }

    @Test
    public void getTimeSlotById_shouldThrowExceptionWhenTimeSlotNotFound() {

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        TimeSlotNotFoundException exception = assertThrows(TimeSlotNotFoundException.class,
                () -> timeslotService.getTimeSlotById(doctor.getId(), timeSlot.getId()));

        assertEquals("Timeslot not found.", exception.getMessage());
        verify(timeSlotRepository, times(1)).findById(1L);
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnTimeSlotsSuccessfully() {

         SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(1L);
        timeSlot1.setDoctor(doctor);

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setId(2L);
        timeSlot2.setDoctor(doctor);

        List<TimeSlot> timeSlots = List.of(timeSlot1, timeSlot2);

        when(timeSlotRepository.findAllByDoctorId(doctor.getId())).thenReturn(timeSlots);
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        List<TimeSlot> result = timeslotService.getAllTimeSlotsByDoctorId(doctor.getId());

        assertEquals(timeSlots, result);

        verify(doctorRepository,times(1)).findByEmail(doctor.getEmail());
        verify(timeSlotRepository, times(1)).findAllByDoctorId(doctor.getId());
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnEmptyListWhenDoctorHasNoTimeSlots() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        List<TimeSlot> timeSlots = List.of();

        when(timeSlotRepository.findAllByDoctorId(doctor.getId())).thenReturn(timeSlots);
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        List<TimeSlot> result = timeslotService.getAllTimeSlotsByDoctorId(doctor.getId());

        assertEquals(timeSlots, result);
        assertTrue(result.isEmpty());
        verify(doctorRepository,times(1)).findByEmail(doctor.getEmail());
        verify(timeSlotRepository, times(1)).findAllByDoctorId(doctor.getId());

    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldThrowExceptionWhenDoctorNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        when(doctorRepository.findByEmail("doctor@test.mail")).thenThrow(new DoctorNotFoundException(
                "Doctor with email doctor@test.mail is not found."));

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> timeslotService.getAllTimeSlotsByDoctorId(doctor.getId()));

        assertEquals("Doctor with email doctor@test.mail is not found.", exception.getMessage());
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnAvailableTimeSlotsSuccessfully() {

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(1L);
        timeSlot1.setDoctor(doctor);
        timeSlot1.setStatus(TimeSlotStatus.FREE);

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setId(2L);
        timeSlot2.setDoctor(doctor);
        timeSlot2.setStatus(TimeSlotStatus.FREE);

        List<TimeSlot> timeSlots = List.of(timeSlot1, timeSlot2);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findByDoctorIdAndStatus(doctor.getId(), TimeSlotStatus.FREE)).thenReturn(timeSlots);

        List<TimeSlot> result = timeslotService.getAvailableTimeSlotsByDoctorId(doctor.getId());

        assertEquals(timeSlots, result);

        verify(doctorRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findByDoctorIdAndStatus(doctor.getId(), TimeSlotStatus.FREE);
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots() {

        List<TimeSlot> timeSlots = List.of();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findByDoctorIdAndStatus(doctor.getId(), TimeSlotStatus.FREE)).thenReturn(timeSlots);

        List<TimeSlot> result = timeslotService.getAvailableTimeSlotsByDoctorId(doctor.getId());

        assertEquals(timeSlots, result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findByDoctorIdAndStatus(doctor.getId(), TimeSlotStatus.FREE);
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldThrowExceptionWhenDoctorNotFound() {

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> timeslotService.getAvailableTimeSlotsByDoctorId(doctor.getId()));

        assertEquals("Doctor with id 1 is not found.", exception.getMessage());
        verify(doctorRepository, times(1)).findById(1L);
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnAvailableTimeSlotsSuccessfully() {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(1L);
        timeSlot1.setDate(futureDate);
        timeSlot1.setStatus(TimeSlotStatus.FREE);
        timeSlot1.setDoctor(doctor);

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setId(2L);
        timeSlot2.setDate(futureDate);
        timeSlot2.setStatus(TimeSlotStatus.FREE);
        timeSlot2.setDoctor(doctor);

        List<TimeSlot> timeSlots = List.of(timeSlot1, timeSlot2);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findAllByDoctorIdAndDateAndStatus(doctor.getId(), timeSlot1.getDate(), TimeSlotStatus.FREE)).thenReturn(timeSlots);

        List<TimeSlot> result = timeslotService.getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(), timeSlot1.getDate());

        assertEquals(timeSlots, result);

        verify(doctorRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findAllByDoctorIdAndDateAndStatus(doctor.getId(), timeSlot1.getDate(), TimeSlotStatus.FREE);
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots() {

        LocalDate date = LocalDate.now().plusDays(1);

        List<TimeSlot> timeSlots = List.of();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findAllByDoctorIdAndDateAndStatus(doctor.getId(), date, TimeSlotStatus.FREE)).thenReturn(timeSlots);

        List<TimeSlot> result = timeslotService.getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(), date);

        assertEquals(timeSlots, result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).findAllByDoctorIdAndDateAndStatus(doctor.getId(), date, TimeSlotStatus.FREE);
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldThrowExceptionWhenDoctorNotFound() {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> timeslotService.getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(), futureDate));

        assertEquals("Doctor with id 1 is not found.", exception.getMessage());

        verify(doctorRepository, times(1)).findById(1L);
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldThrowExceptionWhenDateIsInThePast() {

        LocalDate pastDate = LocalDate.now().minusDays(1);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(), pastDate));

        assertEquals("Date cannot be in the past.", exception.getMessage());

        verify(doctorRepository, times(1)).findById(1L);
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    public void blockTimeSlot_shouldBlockFreeTimeSlotSuccessfully() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(futureDate);
        timeSlot.setStatus(TimeSlotStatus.FREE);
        timeSlot.setDoctor(doctor);

        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(timeSlot)).thenReturn(timeSlot);

        TimeSlot result = timeslotService.blockTimeSlot(doctor.getId(), timeSlot.getId());

        assertEquals(timeSlot, result);
        assertEquals(TimeSlotStatus.BLOCKED, result.getStatus());

        verify(timeSlotRepository, times(1)).save(timeSlot);
        verify(timeSlotRepository, times(1)).findById(1L);
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        TimeSlotNotFoundException exception = assertThrows(TimeSlotNotFoundException.class,
                () -> timeslotService.blockTimeSlot(doctor.getId(), 1L));

        assertEquals("Timeslot not found.", exception.getMessage());
        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotDoesNotBelongToDoctor() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        Doctor faceDoctor = new Doctor();
        faceDoctor.setId(2L);

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(futureDate);
        timeSlot.setStatus(TimeSlotStatus.FREE);
        timeSlot.setDoctor(faceDoctor);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        TimeSlotDoesNotBelongToDoctorException exception = assertThrows(TimeSlotDoesNotBelongToDoctorException.class,
                () -> timeslotService.blockTimeSlot(doctor.getId(), timeSlot.getId()));

        assertEquals("Timeslot with id 1 does not belong to doctor with id 1", exception.getMessage());
        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotAlreadyBlocked() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(futureDate);
        timeSlot.setStatus(TimeSlotStatus.BLOCKED);
        timeSlot.setDoctor(doctor);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.blockTimeSlot(doctor.getId(), timeSlot.getId()));

        assertEquals("Timeslot is already blocked.", exception.getMessage());

        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotIsBooked() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(futureDate);
        timeSlot.setStatus(TimeSlotStatus.BOOKED);
        timeSlot.setDoctor(doctor);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.blockTimeSlot(doctor.getId(), timeSlot.getId()));

        assertEquals("Cannot block booked time slot.", exception.getMessage());

        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    public void deleteTimeSlot_shouldDeleteTimeSlotSuccessfully() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(futureDate);
        timeSlot.setStatus(TimeSlotStatus.BLOCKED);
        timeSlot.setDoctor(doctor);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        timeslotService.deleteTimeSlot(doctor.getId(), timeSlot.getId());

        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).delete(timeSlot);
    }

    @Test
    public void deleteTimeSlot_shouldThrowExceptionWhenTimeSlotNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        TimeSlotNotFoundException exception = assertThrows(TimeSlotNotFoundException.class,
                () -> timeslotService.deleteTimeSlot(doctor.getId(), timeSlot.getId()));

        assertEquals("TimeSlot with id 1 is not found.", exception.getMessage());
        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, never()).delete(any(TimeSlot.class));
    }

    @Test
    public void deleteTimeSlot_shouldThrowExceptionWhenTimeSlotIsBooked() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.mail",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );

        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(futureDate);
        timeSlot.setStatus(TimeSlotStatus.BOOKED);
        timeSlot.setDoctor(doctor);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(doctorRepository.findByEmail("doctor@test.mail")).thenReturn(Optional.of(doctor));

        InvalidTimeSlotException exception = assertThrows(InvalidTimeSlotException.class,
                () -> timeslotService.deleteTimeSlot(doctor.getId(), timeSlot.getId()));

        assertEquals("Cannot delete booked time slot.", exception.getMessage());

        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, never()).delete(any(TimeSlot.class));
    }

}

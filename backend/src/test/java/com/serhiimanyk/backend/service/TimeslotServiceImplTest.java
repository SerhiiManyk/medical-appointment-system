package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.InvalidTimeSlotException;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.impl.TimeslotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    }

    @BeforeEach
    public void createTestTimeSlot() {
        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStatus(TimeSlotStatus.FREE);
        timeSlot.setDoctor(doctor);
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

        assertEquals(timeSlot, result );
        assertEquals(doctor, result.getDoctor());
        assertEquals(TimeSlotStatus.FREE, result.getStatus());

        verify(doctorRepository, times (1)).findById(1L);
        verify(timeSlotRepository,times(1)).existsByDoctorIdAndDateAndStartTimeAndEndTime(1L, timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime());
        verify(timeSlotRepository,times(1)).save(timeSlot);
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
    }

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenDoctorNotFound() {
    }

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenTimeSlotAlreadyExists() {
    }

    @Test
    public void getTimeSlotById_shouldReturnTimeSlotSuccessfully() {
    }

    @Test
    public void getTimeSlotById_shouldThrowExceptionWhenTimeSlotNotFound() {
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnTimeSlotsSuccessfully() {
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnEmptyListWhenDoctorHasNoTimeSlots() {
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldThrowExceptionWhenDoctorNotFound() {
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnAvailableTimeSlotsSuccessfully() {
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots() {
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldThrowExceptionWhenDoctorNotFound() {
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnAvailableTimeSlotsSuccessfully() {
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots() {
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldThrowExceptionWhenDoctorNotFound() {
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldThrowExceptionWhenDateIsInThePast() {
    }

    @Test
    public void blockTimeSlot_shouldBlockFreeTimeSlotSuccessfully() {
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotNotFound() {
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotDoesNotBelongToDoctor() {
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotAlreadyBlocked() {
    }

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotIsBooked() {
    }

    @Test
    public void deleteTimeSlot_shouldDeleteTimeSlotSuccessfully() {
    }

    @Test
    public void deleteTimeSlot_shouldThrowExceptionWhenTimeSlotNotFound() {
    }

    @Test
    public void deleteTimeSlot_shouldThrowExceptionWhenTimeSlotIsBooked() {
    }

}

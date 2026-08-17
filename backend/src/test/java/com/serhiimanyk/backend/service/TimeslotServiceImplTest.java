package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.impl.TimeslotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    public void createTimeSlot_shouldCreateTimeSlotSuccessfully(){}

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenEndTimeIsBeforeStartTime(){}

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenDateIsInThePast(){}

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenDoctorNotFound(){}

    @Test
    public void createTimeSlot_shouldThrowExceptionWhenTimeSlotAlreadyExists(){}

    @Test
    public void getTimeSlotById_shouldReturnTimeSlotSuccessfully(){}

    @Test
    public void getTimeSlotById_shouldThrowExceptionWhenTimeSlotNotFound(){}

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnTimeSlotsSuccessfully(){}

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnEmptyListWhenDoctorHasNoTimeSlots(){}

    @Test
    public void getAllTimeSlotsByDoctorId_shouldThrowExceptionWhenDoctorNotFound(){}

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnAvailableTimeSlotsSuccessfully(){}

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots(){}

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldThrowExceptionWhenDoctorNotFound(){}

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnAvailableTimeSlotsSuccessfully(){}

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots(){}

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldThrowExceptionWhenDoctorNotFound(){}

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldThrowExceptionWhenDateIsInThePast(){}

    @Test
    public void blockTimeSlot_shouldBlockFreeTimeSlotSuccessfully(){}

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotNotFound(){}

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotDoesNotBelongToDoctor(){}

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotAlreadyBlocked(){}

    @Test
    public void blockTimeSlot_shouldThrowExceptionWhenTimeSlotIsBooked(){}

    @Test
    public void deleteTimeSlot_shouldDeleteTimeSlotSuccessfully(){}

    @Test
    public void deleteTimeSlot_shouldThrowExceptionWhenTimeSlotNotFound(){}

    @Test
    public void deleteTimeSlot_shouldThrowExceptionWhenTimeSlotIsBooked(){}

}

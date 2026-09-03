package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.TimeSlotRequest;
import com.serhiimanyk.backend.dto.response.TimeSlotResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.handler.GlobalExceptionHandler;
import com.serhiimanyk.backend.mapper.TimeSlotMapper;
import com.serhiimanyk.backend.service.TimeSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class TimeSlotControllerTest {

    private MockMvc mockMvc;
    private TimeSlot timeSlot;
    private TimeSlotRequest timeSlotRequest;
    private TimeSlotResponse timeSlotResponse;

    @InjectMocks
    TimeSlotController timeSlotController;

    @Mock
    TimeSlotService timeSlotService;

    @Mock
    TimeSlotMapper timeSlotMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(timeSlotController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        timeSlot = new TimeSlot();
        timeSlot.setDoctor(doctor);
        timeSlot.setDate( LocalDate.of(2030,7,11));
        timeSlot.setStartTime(LocalTime.of(11, 0));
        timeSlot.setEndTime(LocalTime.of(11, 30));
        timeSlot.setStatus(TimeSlotStatus.FREE);

         timeSlotResponse = new TimeSlotResponse(
                1L,
                timeSlot.getDate(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                timeSlot.getStatus(),
                timeSlot.getDoctor().getId()
        );

        timeSlotRequest = new TimeSlotRequest();
        timeSlotRequest.setDate(timeSlot.getDate());
        timeSlotRequest.setStartTime(timeSlot.getStartTime());
        timeSlotRequest.setEndTime(timeSlot.getEndTime());
    }
}

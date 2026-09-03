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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TimeSlotControllerTest {

    private MockMvc mockMvc;
    private Doctor doctor;
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

        doctor = new Doctor();
        doctor.setId(1L);

        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDoctor(doctor);
        timeSlot.setDate(LocalDate.of(2030, 7, 11));
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

    @Test
    public void createTimeSlot_shouldCreateTimeSlotSuccessfully() throws Exception {

        when(timeSlotMapper.toTimeSlot(any(TimeSlotRequest.class))).thenReturn(timeSlot);
        when(timeSlotMapper.toTimeSlotResponse(timeSlot)).thenReturn(timeSlotResponse);
        when(timeSlotService.createTimeSlot(doctor.getId(), timeSlot)).thenReturn(timeSlot);

        mockMvc.perform(
                        post("/api/doctors/1/timeslots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "date": "2030-07-11",
                                        "startTime": "11:00",
                                        "endTime": "11:30"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/doctors/1/timeslots/1"))
                .andExpect(jsonPath("$.date").value("2030-07-11"))
                .andExpect(jsonPath("$.startTime").value("11:00"))
                .andExpect(jsonPath("$.endTime").value("11:30"))
                .andExpect(jsonPath("$.doctorId").value(1L))
                .andExpect(jsonPath("$.status").value("FREE"));

        verify(timeSlotMapper, times(1)).toTimeSlot(any(TimeSlotRequest.class));
        verify(timeSlotMapper, times(1)).toTimeSlotResponse(timeSlot);
        verify(timeSlotService, times(1)).createTimeSlot(doctor.getId(), timeSlot);
    }
}

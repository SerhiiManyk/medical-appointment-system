package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.TimeSlotRequest;
import com.serhiimanyk.backend.dto.response.TimeSlotResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.InvalidTimeSlotException;
import com.serhiimanyk.backend.exception.TimeSlotNotFoundException;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void createTimeSlot_shouldReturn400WhenEndTimeIsBeforeStartTime() throws Exception {

        timeSlot.setEndTime(LocalTime.of(11, 0));
        timeSlot.setStartTime(LocalTime.of(11, 30));

        when(timeSlotMapper.toTimeSlot(any(TimeSlotRequest.class))).thenReturn(timeSlot);
        when(timeSlotService.createTimeSlot(doctor.getId(), timeSlot)).thenThrow(new InvalidTimeSlotException(
                "Time slot end time is before time slot start time."
        ));

        mockMvc.perform(
                        post("/api/doctors/1/timeslots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "date": "2030-07-11",
                                        "startTime": "11:30",
                                        "endTime": "11:00"
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Time slot end time is before time slot start time."));
        verify(timeSlotMapper, times(1)).toTimeSlot(any(TimeSlotRequest.class));
        verify(timeSlotService, times(1)).createTimeSlot(doctor.getId(), timeSlot);
    }

    @Test
    public void createTimeSlot_shouldReturn400WhenDateIsInThePast() throws Exception {

        timeSlot.setDate(LocalDate.of(2000, 1, 1));

        when(timeSlotMapper.toTimeSlot(any(TimeSlotRequest.class))).thenReturn(timeSlot);
        when(timeSlotService.createTimeSlot(doctor.getId(), timeSlot)).thenThrow(new InvalidTimeSlotException(
                "Time slot date is before time slot start date."
        ));

        mockMvc.perform(
                        post("/api/doctors/1/timeslots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "date": "2000-01-01",
                                        "startTime": "11:00",
                                        "endTime": "11:30"
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Time slot date is before time slot start date."));

        verify(timeSlotMapper, times(1)).toTimeSlot(any(TimeSlotRequest.class));
        verify(timeSlotService, times(1)).createTimeSlot(doctor.getId(), timeSlot);
    }

    @Test
    public void createTimeSlot_shouldReturn400WhenTimeSlotAlreadyExists() throws Exception {

        when(timeSlotMapper.toTimeSlot(any(TimeSlotRequest.class))).thenReturn(timeSlot);
        when(timeSlotService.createTimeSlot(doctor.getId(), timeSlot)).thenThrow(new InvalidTimeSlotException(
                "Time slot already exists."
        ));

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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Time slot already exists."));

        verify(timeSlotMapper, times(1)).toTimeSlot(any(TimeSlotRequest.class));
        verify(timeSlotService, times(1)).createTimeSlot(doctor.getId(), timeSlot);
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnTimeSlotsSuccessfully() throws Exception {

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(2L);
        timeSlot1.setDoctor(doctor);
        timeSlot1.setStartTime(LocalTime.of(10, 0));
        timeSlot1.setEndTime(LocalTime.of(10, 30));
        timeSlot1.setDate(LocalDate.of(2030, 1, 1));
        timeSlot1.setStatus(TimeSlotStatus.FREE);

        List<TimeSlot> timeSlots = List.of(timeSlot, timeSlot1);

        List<TimeSlotResponse> timeSlotResponses = List.of(
                new TimeSlotResponse(
                        timeSlot.getId(),
                        timeSlot.getDate(),
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime(),
                        timeSlot.getStatus(),
                        timeSlot.getDoctor().getId()
                ),
                new TimeSlotResponse(
                        timeSlot1.getId(),
                        timeSlot1.getDate(),
                        timeSlot1.getStartTime(),
                        timeSlot1.getEndTime(),
                        timeSlot1.getStatus(),
                        timeSlot1.getDoctor().getId()
                )
        );

        when(timeSlotService.getAllTimeSlotsByDoctorId(doctor.getId())).thenReturn(timeSlots);
        when(timeSlotMapper.toTimeSlotResponseList(timeSlots)).thenReturn(timeSlotResponses);

        mockMvc.perform(
                        get("/api/doctors/1/timeslots")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2030-07-11"))
                .andExpect(jsonPath("$[0].startTime").value("11:00"))
                .andExpect(jsonPath("$[0].endTime").value("11:30"))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].status").value("FREE"))
                .andExpect(jsonPath("$[1].date").value("2030-01-01"))
                .andExpect(jsonPath("$[1].startTime").value("10:00"))
                .andExpect(jsonPath("$[1].endTime").value("10:30"))
                .andExpect(jsonPath("$[1].doctorId").value(1L))
                .andExpect(jsonPath("$[1].status").value("FREE"));

        verify(timeSlotMapper, times(1)).toTimeSlotResponseList(timeSlots);
        verify(timeSlotService, times(1)).getAllTimeSlotsByDoctorId(doctor.getId());
    }

    @Test
    public void getAllTimeSlotsByDoctorId_shouldReturnEmptyListWhenDoctorHasNoTimeSlots() throws Exception {

        List<TimeSlot> timeSlots = List.of();
        List<TimeSlotResponse> timeSlotResponses = List.of();

        when(timeSlotService.getAllTimeSlotsByDoctorId(doctor.getId())).thenReturn(timeSlots);
        when(timeSlotMapper.toTimeSlotResponseList(timeSlots)).thenReturn(timeSlotResponses);

        mockMvc.perform(
                        get("/api/doctors/1/timeslots")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(timeSlotMapper, times(1)).toTimeSlotResponseList(timeSlots);
        verify(timeSlotService, times(1)).getAllTimeSlotsByDoctorId(doctor.getId());
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnAvailableTimeSlotsSuccessfully()  throws Exception {

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(2L);
        timeSlot1.setDoctor(doctor);
        timeSlot1.setStartTime(LocalTime.of(10, 0));
        timeSlot1.setEndTime(LocalTime.of(10, 30));
        timeSlot1.setDate(LocalDate.of(2030, 1, 1));
        timeSlot1.setStatus(TimeSlotStatus.FREE);

        List<TimeSlot> timeSlots = List.of(timeSlot, timeSlot1);

        List<TimeSlotResponse> timeSlotResponses = List.of(
                new TimeSlotResponse(
                        timeSlot.getId(),
                        timeSlot.getDate(),
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime(),
                        timeSlot.getStatus(),
                        timeSlot.getDoctor().getId()
                ),
                new TimeSlotResponse(
                        timeSlot1.getId(),
                        timeSlot1.getDate(),
                        timeSlot1.getStartTime(),
                        timeSlot1.getEndTime(),
                        timeSlot1.getStatus(),
                        timeSlot1.getDoctor().getId()
                )
        );

        when(timeSlotService.getAvailableTimeSlotsByDoctorId(doctor.getId())).thenReturn(timeSlots);
        when(timeSlotMapper.toTimeSlotResponseList(timeSlots)).thenReturn(timeSlotResponses);

        mockMvc.perform(
                        get("/api/doctors/1/timeslots/available")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2030-07-11"))
                .andExpect(jsonPath("$[0].startTime").value("11:00"))
                .andExpect(jsonPath("$[0].endTime").value("11:30"))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].status").value("FREE"))
                .andExpect(jsonPath("$[1].date").value("2030-01-01"))
                .andExpect(jsonPath("$[1].startTime").value("10:00"))
                .andExpect(jsonPath("$[1].endTime").value("10:30"))
                .andExpect(jsonPath("$[1].doctorId").value(1L))
                .andExpect(jsonPath("$[1].status").value("FREE"))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(timeSlotMapper, times(1)).toTimeSlotResponseList(timeSlots);
        verify(timeSlotService, times(1)).getAvailableTimeSlotsByDoctorId(doctor.getId());
    }

    @Test
    public void getAvailableTimeSlotsByDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots()  throws Exception{

        List<TimeSlot> timeSlots = List.of();
        List<TimeSlotResponse> timeSlotResponses = List.of();

        when(timeSlotService.getAvailableTimeSlotsByDoctorId(doctor.getId())).thenReturn(timeSlots);
        when(timeSlotMapper.toTimeSlotResponseList(timeSlots)).thenReturn(timeSlotResponses);

        mockMvc.perform(
                        get("/api/doctors/1/timeslots/available")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(timeSlotMapper, times(1)).toTimeSlotResponseList(timeSlots);
        verify(timeSlotService, times(1)).getAvailableTimeSlotsByDoctorId(doctor.getId());
    }

    @Test
    public void getTimeSlotById_shouldReturnTimeSlotSuccessfully()  throws Exception {

        when(timeSlotService.getTimeSlotById(doctor.getId(), timeSlot.getId())).thenReturn(timeSlot);
        when(timeSlotMapper.toTimeSlotResponse(timeSlot)).thenReturn(timeSlotResponse);

        mockMvc.perform(
                get("/api/doctors/1/timeslots/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2030-07-11"))
                .andExpect(jsonPath("$.startTime").value("11:00"))
                .andExpect(jsonPath("$.endTime").value("11:30"))
                .andExpect(jsonPath("$.doctorId").value(1L))
                .andExpect(jsonPath("$.status").value("FREE"));

        verify(timeSlotMapper, times(1)).toTimeSlotResponse(timeSlot);
        verify(timeSlotService, times(1)).getTimeSlotById(doctor.getId(), timeSlot.getId());
    }

    @Test
    public void getTimeSlotById_shouldReturn404WhenTimeSlotNotFound()  throws Exception {

        when(timeSlotService.getTimeSlotById(doctor.getId(), timeSlot.getId())).thenThrow(new TimeSlotNotFoundException("Timeslot not found."));

        mockMvc.perform(
                get("/api/doctors/1/timeslots/1")
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Timeslot not found."));

        verify(timeSlotService, times(1)).getTimeSlotById(doctor.getId(), timeSlot.getId());
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnAvailableTimeSlotsSuccessfully() throws Exception {

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(2L);
        timeSlot1.setDoctor(doctor);
        timeSlot1.setStartTime(LocalTime.of(10, 0));
        timeSlot1.setEndTime(LocalTime.of(10, 30));
        timeSlot1.setDate(LocalDate.of(2030, 7, 11));
        timeSlot1.setStatus(TimeSlotStatus.FREE);

        List<TimeSlot> timeSlots = List.of(timeSlot, timeSlot1);

        List<TimeSlotResponse> timeSlotResponses = List.of(
                new TimeSlotResponse(
                        timeSlot.getId(),
                        timeSlot.getDate(),
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime(),
                        timeSlot.getStatus(),
                        timeSlot.getDoctor().getId()
                ),
                new TimeSlotResponse(
                        timeSlot1.getId(),
                        timeSlot1.getDate(),
                        timeSlot1.getStartTime(),
                        timeSlot1.getEndTime(),
                        timeSlot1.getStatus(),
                        timeSlot1.getDoctor().getId()
                )
        );

        when(timeSlotService.getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(),timeSlot.getDate())).thenReturn(timeSlots);
        when(timeSlotMapper.toTimeSlotResponseList(timeSlots)).thenReturn(timeSlotResponses);

        mockMvc.perform(
                        get("/api/doctors/1/timeslots/available/by-date?date=2030-07-11")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2030-07-11"))
                .andExpect(jsonPath("$[0].startTime").value("11:00"))
                .andExpect(jsonPath("$[0].endTime").value("11:30"))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].status").value("FREE"))
                .andExpect(jsonPath("$[1].date").value("2030-07-11"))
                .andExpect(jsonPath("$[1].startTime").value("10:00"))
                .andExpect(jsonPath("$[1].endTime").value("10:30"))
                .andExpect(jsonPath("$[1].doctorId").value(1L))
                .andExpect(jsonPath("$[1].status").value("FREE"))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(timeSlotMapper, times(1)).toTimeSlotResponseList(timeSlots);
        verify(timeSlotService, times(1)).getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(),timeSlot.getDate());
    }

    @Test
    public void getAvailableTimeSlotsByDateAndDoctorId_shouldReturnEmptyListWhenNoAvailableTimeSlots()  throws Exception{
        List<TimeSlot> timeSlots = List.of();
        List<TimeSlotResponse> timeSlotResponses = List.of();

        when(timeSlotService.getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(),timeSlot.getDate())).thenReturn(timeSlots);
        when(timeSlotMapper.toTimeSlotResponseList(timeSlots)).thenReturn(timeSlotResponses);

        mockMvc.perform(
                get("/api/doctors/1/timeslots/available/by-date?date=2030-07-11")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(timeSlotMapper, times(1)).toTimeSlotResponseList(timeSlots);
        verify(timeSlotService, times(1)).getAvailableTimeSlotsByDateAndDoctorId(doctor.getId(),timeSlot.getDate());
    }
}

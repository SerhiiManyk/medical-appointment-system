package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.TimeSlotRequest;
import com.serhiimanyk.backend.dto.response.TimeSlotResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeSlotMapperTest {

    private TimeSlotMapper timeSlotMapper;
    private TimeSlot timeSlot;
    private Doctor doctor;

    @BeforeEach
    public void setup() {

        timeSlotMapper = new TimeSlotMapper();

        doctor = new Doctor();
        doctor.setId(1L);

        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDoctor(doctor);
        timeSlot.setDate(LocalDate.of(2030, 1, 1));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(10, 0));
        timeSlot.setStatus(TimeSlotStatus.FREE);
    }

    @Test
    public void toTimeSlotResponse_shouldMapTimeSlotToTimeSlotResponse() {

        TimeSlotResponse timeSlotResponse = timeSlotMapper.toTimeSlotResponse(timeSlot);

        assertEquals(timeSlot.getId(), timeSlotResponse.getId());
        assertEquals(timeSlot.getDate(), timeSlotResponse.getDate());
        assertEquals(timeSlot.getStartTime(), timeSlotResponse.getStartTime());
        assertEquals(timeSlot.getEndTime(), timeSlotResponse.getEndTime());
        assertEquals(timeSlot.getStatus(), timeSlotResponse.getStatus());
        assertEquals(timeSlot.getDoctor().getId(), timeSlotResponse.getDoctorId());
    }

    @Test
    public void toTimeSlotResponseList_shouldMapTimeSlotsToTimeSlotResponseList() {

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setId(2L);
        timeSlot2.setDoctor(doctor);
        timeSlot2.setDate(LocalDate.of(2030, 1, 1));
        timeSlot2.setStartTime(LocalTime.of(8, 0));
        timeSlot2.setEndTime(LocalTime.of(9, 0));
        timeSlot2.setStatus(TimeSlotStatus.FREE);

        List<TimeSlot> timeSlotList = List.of(timeSlot, timeSlot2);

        List<TimeSlotResponse> timeSlotResponseList = timeSlotMapper.toTimeSlotResponseList(timeSlotList);

        assertEquals(2, timeSlotResponseList.size());
        assertEquals(timeSlotList.get(0).getId(), timeSlotResponseList.get(0).getId());
        assertEquals(timeSlotList.get(0).getDate(), timeSlotResponseList.get(0).getDate());
        assertEquals(timeSlotList.get(0).getStartTime(), timeSlotResponseList.get(0).getStartTime());
        assertEquals(timeSlotList.get(0).getEndTime(), timeSlotResponseList.get(0).getEndTime());
        assertEquals(timeSlotList.get(0).getStatus(), timeSlotResponseList.get(0).getStatus());
        assertEquals(timeSlotList.get(0).getDoctor().getId(), timeSlotResponseList.get(0).getDoctorId());
        assertEquals(timeSlotList.get(1).getId(), timeSlotResponseList.get(1).getId());
        assertEquals(timeSlotList.get(1).getDate(), timeSlotResponseList.get(1).getDate());
        assertEquals(timeSlotList.get(1).getStartTime(), timeSlotResponseList.get(1).getStartTime());
        assertEquals(timeSlotList.get(1).getEndTime(), timeSlotResponseList.get(1).getEndTime());
        assertEquals(timeSlotList.get(1).getStatus(), timeSlotResponseList.get(1).getStatus());
        assertEquals(timeSlotList.get(1).getDoctor().getId(), timeSlotResponseList.get(1).getDoctorId());
    }

    @Test
    public void toTimeSlot_shouldMapTimeSlotRequestToTimeSlot() {

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest();
        timeSlotRequest.setDate(LocalDate.of(2030, 1, 1));
        timeSlotRequest.setStartTime(LocalTime.of(9, 0));
        timeSlotRequest.setEndTime(LocalTime.of(10, 0));

        TimeSlot resultTimeSlot = timeSlotMapper.toTimeSlot(timeSlotRequest);

        assertEquals(resultTimeSlot.getDate(), timeSlotRequest.getDate());
        assertEquals(resultTimeSlot.getStartTime(), timeSlotRequest.getStartTime());
        assertEquals(resultTimeSlot.getEndTime(), timeSlotRequest.getEndTime());
    }

    @Test
    public void updateTimeSlotFromRequest_shouldUpdateTimeSlotFields() {

        TimeSlotRequest timeSlotRequest = new TimeSlotRequest();
        timeSlotRequest.setDate(LocalDate.of(2030, 1, 1));
        timeSlotRequest.setStartTime(LocalTime.of(11, 0));
        timeSlotRequest.setEndTime(LocalTime.of(12, 0));

        timeSlotMapper.updateTimeSlotFromRequest(timeSlotRequest, timeSlot);

        assertEquals(timeSlot.getDate(), timeSlotRequest.getDate());
        assertEquals(timeSlot.getStartTime(), timeSlotRequest.getStartTime());
        assertEquals(timeSlot.getEndTime(), timeSlotRequest.getEndTime());
    }
}

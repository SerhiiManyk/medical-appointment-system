package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.TimeSlotRequest;
import com.serhiimanyk.backend.dto.response.TimeSlotResponse;
import com.serhiimanyk.backend.entity.TimeSlot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimeSlotMapper {

    public TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot) {

        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .date(timeSlot.getDate())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .status(timeSlot.getStatus())
                .doctorId(timeSlot.getDoctor().getId())
                .build();
    }

    public List<TimeSlotResponse> toTimeSlotResponseList(List<TimeSlot> timeSlots) {

        return timeSlots.stream()
                .map(this::toTimeSlotResponse)
                .toList();
    }

    public  TimeSlot toTimeSlot(TimeSlotRequest timeSlotRequest) {

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDate(timeSlotRequest.getDate());
        timeSlot.setStartTime(timeSlotRequest.getStartTime());
        timeSlot.setEndTime(timeSlotRequest.getEndTime());

        return timeSlot;
    }

    public void updateTimeSlotFromRequest(TimeSlotRequest timeSlotRequest, TimeSlot timeSlot) {

        timeSlot.setDate(timeSlotRequest.getDate());
        timeSlot.setStartTime(timeSlotRequest.getStartTime());
        timeSlot.setEndTime(timeSlotRequest.getEndTime());
    }
}

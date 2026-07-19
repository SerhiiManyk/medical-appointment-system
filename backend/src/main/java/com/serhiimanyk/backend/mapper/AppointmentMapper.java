package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;

import java.util.List;

public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .doctorId(appointment.getDoctor().getId())
                .timeSlotId(appointment.getTimeSlot().getId())
                .date(appointment.getTimeSlot().getDate())
                .startTime(appointment.getTimeSlot().getStartTime())
                .endTime(appointment.getTimeSlot().getEndTime())
                .status(appointment.getStatus())
                .build();
    }

    public List<AppointmentResponse> toResponseList(
            List<Appointment> appointments
    ) {
        return appointments.stream()
                .map(this::toResponse)
                .toList();
    }
}

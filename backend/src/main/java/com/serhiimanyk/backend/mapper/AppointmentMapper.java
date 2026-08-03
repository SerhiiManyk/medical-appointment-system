package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

    public Appointment toAppointment(
            Doctor doctor,
            Patient patient,
            TimeSlot timeSlot
    ) {
        Appointment appointment = new Appointment();

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CREATED);

        return appointment;
    }
}

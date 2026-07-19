package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.CreateAppointmentRequest;
import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.mapper.AppointmentMapper;
import com.serhiimanyk.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    public AppointmentResponse createAppointment(
            @RequestBody CreateAppointmentRequest request
    ) {

        Appointment appointment = appointmentService.createAppointment(
                request.getPatientId(),
                request.getDoctorId(),
                request.getTimeSlotId()
        );

        return appointmentMapper.toResponse(appointment);
    }
}



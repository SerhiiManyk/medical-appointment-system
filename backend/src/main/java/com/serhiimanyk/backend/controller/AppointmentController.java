package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.mapper.AppointmentMapper;
import com.serhiimanyk.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    public AppointmentResponse createAppointment(
            @RequestBody AppointmentCreateRequest request
    ) {

        Appointment appointment = appointmentService.createAppointment(request);

        return appointmentMapper.toResponse(appointment);
    }

    @GetMapping("/{id}")
    public AppointmentResponse getAppointmentById(@PathVariable Long id) {

        Appointment appointment = appointmentService.getAppointmentById(id);

        return appointmentMapper.toResponse(appointment);
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> getAppointmentsByPatientId(@PathVariable Long patientId) {

        List<Appointment> appointmentList = appointmentService.getAppointmentsByPatientId(patientId);

        return appointmentMapper.toResponseList(appointmentList);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponse> getAppointmentsByDoctorId(@PathVariable Long doctorId) {

        List<Appointment> appointmentList = appointmentService.getAppointmentsByDoctorId(doctorId);

        return appointmentMapper.toResponseList(appointmentList);
    }

//    PUT /api/appointments/{id}/cancel
//    PUT /api/appointments/{id}/complete
//    PUT /api/appointments/{id}/reschedule
//    DELETE /api/appointments/{id}

}




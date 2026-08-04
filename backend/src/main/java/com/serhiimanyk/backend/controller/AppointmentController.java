package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.mapper.AppointmentMapper;
import com.serhiimanyk.backend.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request
    ) {

        Appointment appointment = appointmentService.createAppointment(request);

        AppointmentResponse response = appointmentMapper.toResponse(appointment);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(appointment.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {

        Appointment appointment = appointmentService.getAppointmentById(id);

        AppointmentResponse response = appointmentMapper.toResponse(appointment);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatientId(@PathVariable Long patientId) {

        List<Appointment> appointmentList = appointmentService.getAppointmentsByPatientId(patientId);

        List<AppointmentResponse> resultList = appointmentMapper.toResponseList(appointmentList);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {

        List<Appointment> appointmentList = appointmentService.getAppointmentsByDoctorId(doctorId);

        List<AppointmentResponse> resultList = appointmentMapper.toResponseList(appointmentList);

        return ResponseEntity.ok(resultList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointmentById(@PathVariable Long id) {

        appointmentService.deleteAppointmentById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointmentById(@PathVariable Long id) {

        Appointment canceledAppointment = appointmentService.cancelAppointment(id);

        AppointmentResponse response = appointmentMapper.toResponse(canceledAppointment);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointmentById(@PathVariable Long id) {

        Appointment completedAppointment = appointmentService.completeAppointment(id);

        AppointmentResponse response = appointmentMapper.toResponse(completedAppointment);

        return ResponseEntity.ok(response);
    }
}




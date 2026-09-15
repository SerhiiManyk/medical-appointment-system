package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(AppointmentCreateRequest request);

    Appointment getAppointmentById(Long id);

    List<Appointment> getAppointmentsByPatientId(Long id);

    List<Appointment> getAppointmentsByDoctorId(Long id);

    List<Appointment> getAllAppointments();

    void deleteAppointmentById(Long id);

    Appointment cancelAppointment(Long id);

    Appointment completeAppointment(Long id);

    Appointment rescheduleAppointment(Long appointmentId, Long newTimeslotId);

}

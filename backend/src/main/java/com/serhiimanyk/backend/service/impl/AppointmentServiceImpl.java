package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.exception.AppointmentNotFoundException;
import com.serhiimanyk.backend.repository.AppointmentRepository;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final TimeSlotRepository timeSlotRepository;

    private final AppointmentRepository appointmentRepository;

    @Override
    public Appointment createAppointment(Long patientId, Long doctorId, Long timeSlotId) {

        return appointmentRepository.save(new Appointment());
    }

    @Override
    public Appointment getAppointmentById(Long id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long id) {
        return List.of();
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long id) {
        return List.of();
    }

    @Override
    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    @Override
    public void deleteAppointmentById(Long id) {

        appointmentRepository.deleteById(id);
    }

    @Override
    public Appointment cancelAppointment(Long id) {
        return null;
    }

    @Override
    public Appointment completeAppointment(Long id) {
        return null;
    }

    @Override
    public Appointment rescheduleAppointment(Long appointmentId, Long newTimeslotId) {
        return null;
    }
}

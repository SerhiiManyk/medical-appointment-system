package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.*;
import com.serhiimanyk.backend.repository.AppointmentRepository;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

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
    public Appointment createAppointment(AppointmentCreateRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + request.getPatientId() + " is not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + request.getDoctorId() + " is not found"));

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException("Timeslot with id " + request.getTimeSlotId() + " is not found"));

        if (timeSlot.getStatus() != TimeSlotStatus.FREE) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available.");
        }
        if (!request.getDoctorId().equals(timeSlot.getDoctor().getId())) {
            throw new TimeSlotDoesNotBelongToDoctorException(
                    "TimeSlot with id " + request.getTimeSlotId() + " does not belong to doctor with id " + request.getDoctorId());
        }
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CREATED);
        timeSlot.setStatus(TimeSlotStatus.BOOKED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long id) {

        patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " is not found"));

        return appointmentRepository.findByPatientId(id);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long id) {

        doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + id + " is not found"));

        return appointmentRepository.findByDoctorId(id);
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

        Appointment appointment = getAppointmentById(id);
        TimeSlot timeSlot = appointment.getTimeSlot();

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentAlreadyFinishedException("Appointment is already cancelled or completed.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        timeSlot.setStatus(TimeSlotStatus.FREE);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment completeAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentAlreadyFinishedException("Appointment is already cancelled or completed.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment rescheduleAppointment(Long appointmentId, Long newTimeslotId) {

        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentAlreadyFinishedException("Appointment is already cancelled or completed.");
        }

        TimeSlot newTimeSlot = timeSlotRepository.findById(newTimeslotId)
                .orElseThrow(() -> new TimeSlotNotFoundException("Time slot with id " + newTimeslotId + " not found"));

        if (newTimeSlot.getStatus() != TimeSlotStatus.FREE) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available.");
        }

        if (!appointment.getDoctor().getId().equals(newTimeSlot.getDoctor().getId())) {
            throw new TimeSlotDoesNotBelongToDoctorException(
                    "TimeSlot with id " + newTimeSlot.getId() + " does not belong to doctor with id " + appointment.getDoctor().getId());
        }

        if (appointment.getTimeSlot().getId().equals(newTimeSlot.getId())) {
            throw new InvalidTimeSlotException("Timeslot with id " + newTimeSlot.getId() + " is already in progress.");
        }

        appointment.getTimeSlot().setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        appointment.setTimeSlot(newTimeSlot);

        return  appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentByIdForPatient (Long id){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Patient curentPatient = patientRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Patient with email " + username + " not found"));

        Appointment appointment = getAppointmentById(id);

        if(!appointment.getPatient().getId().equals(curentPatient.getId())) {
            throw new AccessDeniedException("You don't have access to this appointment");
        }
        return appointment;
    }

    @Override
    public Appointment getAppointmentByIdForDoctor(Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Doctor curentDoctor = doctorRepository.findByEmail(username).orElseThrow(
                () -> new DoctorNotFoundException(
                        "Doctor with email " + username + " not found"));

        Appointment appointment = getAppointmentById(id);

        if(!appointment.getDoctor().getId().equals(curentDoctor.getId())) {
            throw new AccessDeniedException("You don't have access to this appointment");
        }
        return appointment;
    }
}

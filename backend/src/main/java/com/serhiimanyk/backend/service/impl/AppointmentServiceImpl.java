package com.serhiimanyk.backend.service.impl;

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

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + patientId + " is not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + doctorId + " is not found"));

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new TimeSlotNotFoundException("Timeslot with id " + timeSlotId + " is not found"));

        if (timeSlot.getStatus() != TimeSlotStatus.FREE) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available.");
        }
        if (!doctorId.equals(timeSlot.getDoctor().getId())) {
            throw new TimeSlotDoesNotBelongToDoctorException("TimeSlot with id " + timeSlotId + " does not belong to doctor with id " + doctorId);
        }
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
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
        //треба дописати!!!!!!!!
        return null;
    }
}

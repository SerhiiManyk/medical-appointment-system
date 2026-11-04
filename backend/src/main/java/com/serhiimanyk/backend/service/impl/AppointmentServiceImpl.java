package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.Role;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.*;
import com.serhiimanyk.backend.repository.AppointmentRepository;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.AppointmentService;
import com.serhiimanyk.backend.service.DoctorService;
import com.serhiimanyk.backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final TimeSlotRepository timeSlotRepository;

    private final AppointmentRepository appointmentRepository;

    private final PatientService patientService;

    private final DoctorService doctorService;

    @Override
    public Appointment createAppointment(AppointmentCreateRequest request) {

        Patient patient = patientService.getPatientFromAuthentication();

        if (!(patient.getId().equals( request.getPatientId()))) {
            throw new AccessDeniedException("You can only create appointments for yourself.");
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + request.getDoctorId() + " is not found"));

        TimeSlot timeSlot = timeSlotRepository.findTimeSlotByIdWithLock(request.getTimeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException("Timeslot with id " + request.getTimeSlotId() + " is not found"));

        if (timeSlot.getStatus() != TimeSlotStatus.FREE) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available.");
        }
        if (!request.getDoctorId().equals(timeSlot.getDoctor().getId())) {
            throw new TimeSlotDoesNotBelongToDoctorException(
                    "TimeSlot with id " + request.getTimeSlotId() + " does not belong to doctor with id " + request.getDoctorId());
        }
        if (appointmentRepository.existsByTimeSlot_IdAndStatus(timeSlot.getId(), AppointmentStatus.CREATED)) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available."
            );
        }
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CREATED);
        timeSlot.setStatus(TimeSlotStatus.BOOKED);
        return appointmentRepository.save(appointment);
    }

    private Appointment getAppointmentById(Long id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long id) {

        patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " is not found"));

        Role role = getCurrentUserRole();

        if (role == Role.PATIENT) {
            if (!patientService.isCurrentPatient(id)) {
                throw new AccessDeniedException("You can only see your appointments.");
            }
            return appointmentRepository.findByPatientId(id);
        } else if (role == Role.DOCTOR) {
            return appointmentRepository.findByPatientId(id);
        } else {
            throw new AccessDeniedException("You don't have access to these appointments");
        }
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long id) {

        doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + id + " is not found"));

        Role role = getCurrentUserRole();

        if (role == Role.PATIENT) {
            throw new AccessDeniedException("You can only see your appointments.");
        } else if (role == Role.DOCTOR) {
            if (!doctorService.isCurrentDoctor(id)) {
                throw new AccessDeniedException("You can only see your appointments.");
            }
            return appointmentRepository.findByDoctorId(id);
        } else {
            throw new AccessDeniedException("You don't have access to these appointments");
        }
    }

    @Override
    public void deleteAppointmentById(Long id) {

        Appointment appointment = getAppointmentByIdForDoctor(id);

        appointmentRepository.deleteById(appointment.getId());
    }

    @Override
    public Appointment cancelAppointment(Long id) {

        Appointment appointment = getAppointmentForCurrentUser(id);

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
        Appointment appointment = getAppointmentByIdForDoctor(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentAlreadyFinishedException("Appointment is already cancelled or completed.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment rescheduleAppointment(Long appointmentId, Long newTimeslotId) {

        Appointment appointment = getAppointmentForCurrentUser(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppointmentAlreadyFinishedException("Appointment is already cancelled or completed.");
        }

        TimeSlot newTimeSlot = timeSlotRepository.findTimeSlotByIdWithLock(newTimeslotId)
                .orElseThrow(() -> new TimeSlotNotFoundException("Time slot with id " + newTimeslotId + " not found"));

        if (appointment.getTimeSlot().getId().equals(newTimeSlot.getId())) {
            throw new InvalidTimeSlotException("Timeslot with id " + newTimeSlot.getId() + " is already in progress.");
        }
        if (!appointment.getDoctor().getSpecialization().equals(newTimeSlot.getDoctor().getSpecialization())) {
            throw new TimeSlotDoesNotBelongToDoctorException(
                    "Cannot reschedule appointment to a doctor with a different specialization.");
        }
        if (newTimeSlot.getStatus() != TimeSlotStatus.FREE) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available.");
        }
        if (appointmentRepository.existsByTimeSlot_IdAndStatus(newTimeslotId, AppointmentStatus.CREATED)) {
            throw new InvalidTimeSlotException(
                    "TimeSlot is not available."
            );
        }
        if(appointmentRepository.existsByPatient_IdAndTimeSlot_DateAndTimeSlot_StartTimeAndTimeSlot_EndTimeAndStatusAndIdNot(
                appointment.getPatient().getId(),
                newTimeSlot.getDate(),
                newTimeSlot.getStartTime(),
                newTimeSlot.getEndTime(),
                AppointmentStatus.CREATED,
                appointment.getId()
        )){
            throw new InvalidTimeSlotException(
                    "Patient already has an appointment at this time."
            );
        }

        appointment.getTimeSlot().setStatus(TimeSlotStatus.FREE);
        newTimeSlot.setStatus(TimeSlotStatus.BOOKED);
        appointment.setTimeSlot(newTimeSlot);
        appointment.setDoctor(newTimeSlot.getDoctor());

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentByIdForPatient(Long id) {

        Patient curentPatient = patientService.getPatientFromAuthentication();

        Appointment appointment = getAppointmentById(id);

        if (!appointment.getPatient().getId().equals(curentPatient.getId())) {
            throw new AccessDeniedException("You don't have access to this appointment");
        }
        return appointment;
    }

    @Override
    public Appointment getAppointmentByIdForDoctor(Long id) {

        Doctor curentDoctor = doctorService.getDoctorFromAuthentication();

        Appointment appointment = getAppointmentById(id);

        if (!appointment.getDoctor().getId().equals(curentDoctor.getId())) {
            throw new AccessDeniedException("You don't have access to this appointment");
        }
        return appointment;
    }

    private Appointment getAppointmentForCurrentUser(Long appointmentId) {

        Role role = getCurrentUserRole();

        if (role == Role.PATIENT) {
            return getAppointmentByIdForPatient(appointmentId);
        } else if (role == Role.DOCTOR) {
            return getAppointmentByIdForDoctor(appointmentId);
        } else {
            throw new AccessDeniedException("You don't have access to this appointment");
        }

    }

    private Role getCurrentUserRole() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isPatient = authorities
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PATIENT"));

        boolean isDoctor = authorities
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_DOCTOR"));

        if (isPatient) {
            return Role.PATIENT;
        } else if (isDoctor) {
            return Role.DOCTOR;
        } else {
            throw new AccessDeniedException("You don't have access to this appointment");
        }
    }
}

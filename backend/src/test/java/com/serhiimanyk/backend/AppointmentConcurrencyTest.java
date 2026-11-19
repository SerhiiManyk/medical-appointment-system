package com.serhiimanyk.backend;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.*;
import com.serhiimanyk.backend.exception.InvalidTimeSlotException;
import com.serhiimanyk.backend.exception.TimeSlotNotFoundException;
import com.serhiimanyk.backend.repository.AppointmentRepository;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class AppointmentConcurrencyTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private AppointmentRepository  appointmentRepository;

    private Doctor createTestDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("TestFirstName");
        doctor.setLastName("TestLastName");
        doctor.setEmail(UUID.randomUUID() + "@mail.com");
        doctor.setPassword("TestPassword");
        doctor.setPhoneNumber("123456789");
        doctor.setSpecialization(Specialization.DENTIST);
        doctor.setRole(Role.DOCTOR);
        return doctorRepository.save(doctor);
    }

    private Patient createTestPatient(String email) {
        Patient patient = new Patient();
        patient.setFirstName("TestFirstName");
        patient.setLastName("TestLastName");
        patient.setEmail(email);
        patient.setPassword("TestPassword");
        patient.setPhoneNumber("123456789");
        patient.setGender(Gender.MALE);
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setRole(Role.PATIENT);

        return patientRepository.save(patient);
    }

    private TimeSlot createTestTimeSlot(Doctor doctor) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDoctor(doctor);
        timeSlot.setDate(LocalDate.now().plusDays(7));
        timeSlot.setStartTime(LocalTime.of(8, 30));
        timeSlot.setEndTime(LocalTime.of(9, 0));
        timeSlot.setStatus(TimeSlotStatus.FREE);

        return timeSlotRepository.save(timeSlot);
    }

    private Appointment createTestAppointment(Long patientId, Long doctorId, Long timeSlotId) {

        AppointmentCreateRequest appointmentCreateRequest = new AppointmentCreateRequest();
        appointmentCreateRequest.setPatientId(patientId);
        appointmentCreateRequest.setDoctorId(doctorId);
        appointmentCreateRequest.setTimeSlotId(timeSlotId);

        return appointmentService.createAppointment(appointmentCreateRequest);
    }

    private void authenticatePatient(String email) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                email,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
        );

        context.setAuthentication(token);

        SecurityContextHolder.setContext(context);
    }

    @Test
    public void concurrentBooking_shouldAllowOnlyOnePatient() throws Exception {

        Doctor doctor = createTestDoctor();

        Patient patient = createTestPatient(UUID.randomUUID() + "@mail.com");

        Patient patient2 = createTestPatient(UUID.randomUUID() + "@mail.com");

        TimeSlot timeSlot = createTestTimeSlot(doctor);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Future<Boolean> firstBooking = executor.submit(() -> {
            try {
                authenticatePatient(patient.getEmail());
                ready.countDown();
                start.await();
                try {
                    createTestAppointment(patient.getId(), doctor.getId(), timeSlot.getId());
                    return true;
                } catch (InvalidTimeSlotException e) {
                    return false;
                }
            } finally {
                SecurityContextHolder.clearContext();
            }
        });

        Future<Boolean> secondBooking = executor.submit(() -> {
            try {
                authenticatePatient(patient2.getEmail());
                ready.countDown();
                start.await();
                try {
                    createTestAppointment(patient2.getId(), doctor.getId(), timeSlot.getId());
                    return true;
                } catch (InvalidTimeSlotException e) {
                    return false;
                }
            } finally {
                SecurityContextHolder.clearContext();
            }
        });

        try {
            ready.await();
            start.countDown();

            boolean firstResult = firstBooking.get();
            boolean secondResult = secondBooking.get();

            assertNotEquals(firstResult, secondResult);

            TimeSlot resultTimeSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow(
                    () -> new TimeSlotNotFoundException("TimeSlot with id " + timeSlot.getId() + " not found")
            );
            assertEquals(TimeSlotStatus.BOOKED, resultTimeSlot.getStatus());
            assertEquals(1L,appointmentRepository.countByTimeSlot_IdAndStatus(
                    timeSlot.getId(),
                    AppointmentStatus.CREATED));
        } finally {
            executor.shutdown();
        }
    }
}

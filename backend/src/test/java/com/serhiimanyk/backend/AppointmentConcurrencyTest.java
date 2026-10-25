package com.serhiimanyk.backend;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.Gender;
import com.serhiimanyk.backend.enums.Role;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private Doctor createTestDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("TestFirstName");
        doctor.setLastName("TestLastName");
        doctor.setEmail("testDoctorDentist@mail.com");
        doctor.setPassword("TestPassword");
        doctor.setPhoneNumber("123456789");
        doctor.setSpecialization(Specialization.DENTIST);
        doctor.setRole(Role.DOCTOR);
        return doctorRepository.save(doctor);
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setFirstName("TestFirstName");
        patient.setLastName("TestLastName");
        patient.setEmail("testPatientIntegration@mail.com");
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
        timeSlot.setStartTime(LocalTime.of(8,30));
        timeSlot.setEndTime(LocalTime.of(9,0));
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
}

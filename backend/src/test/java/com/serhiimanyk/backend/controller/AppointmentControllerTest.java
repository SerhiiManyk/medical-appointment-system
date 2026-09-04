package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.handler.GlobalExceptionHandler;
import com.serhiimanyk.backend.mapper.AppointmentMapper;
import com.serhiimanyk.backend.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerTest {

    private MockMvc mockMvc;
    private Appointment appointment;
    private Doctor doctor;
    private Patient patient;
    private TimeSlot timeSlot;
    private AppointmentResponse appointmentResponse;
    private AppointmentCreateRequest appointmentCreateRequest;

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AppointmentMapper appointmentMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(appointmentController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Doctor");
        doctor.setLastName("Watson");
        doctor.setSpecialization(Specialization.FAMILY_DOCTOR);

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");

        timeSlot = new TimeSlot();
        timeSlot.setDoctor(doctor);
        timeSlot.setId(1L);
        timeSlot.setDate(LocalDate.of(2030, 1, 1));
        timeSlot.setStartTime(LocalTime.of(8, 0));
        timeSlot.setEndTime(LocalTime.of(9, 0));

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setComment("Test comment");

        appointmentResponse = new AppointmentResponse(
                appointment.getId(),

                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),

                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization(),

                timeSlot.getId(),
                timeSlot.getDate(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),

                appointment.getStatus(),
                appointment.getComment()
        );

        appointmentCreateRequest = new AppointmentCreateRequest();
        appointmentCreateRequest.setPatientId(patient.getId());
        appointmentCreateRequest.setDoctorId(doctor.getId());
        appointmentCreateRequest.setTimeSlotId(timeSlot.getId());
    }

    @Test
    public void createAppointment_shouldCreateAppointmentSuccessfully() throws Exception {

        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        mockMvc.perform(
                post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                "patientId":"1",
                                "doctorId":"1",
                                "timeSlotId":"1"
                                 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/appointments/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.patientFirstName").value("John"))
                .andExpect(jsonPath("$.patientLastName").value("Doe"))
                .andExpect(jsonPath("$.doctorId").value(1L))
                .andExpect(jsonPath("$.doctorFirstName").value("Doctor"))
                .andExpect(jsonPath("$.doctorLastName").value("Watson"))
                .andExpect(jsonPath("$.specialization").value("FAMILY_DOCTOR"))
                .andExpect(jsonPath("$.timeSlotId").value(1L))
                .andExpect(jsonPath("$.date").value("2030-01-01"))
                .andExpect(jsonPath("$.startTime").value("08:00"))
                .andExpect(jsonPath("$.endTime").value("09:00"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.comment").value("Test comment"));

        verify(appointmentService,times(1)).createAppointment(any(AppointmentCreateRequest.class));
        verify(appointmentMapper,times(1)).toResponse(appointment);
    }

    @Test
    public void getAppointmentById_shouldReturnAppointmentSuccessfully()  throws Exception {

        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        mockMvc.perform(
                        get("/api/appointments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.patientFirstName").value("John"))
                .andExpect(jsonPath("$.patientLastName").value("Doe"))
                .andExpect(jsonPath("$.doctorId").value(1L))
                .andExpect(jsonPath("$.doctorFirstName").value("Doctor"))
                .andExpect(jsonPath("$.doctorLastName").value("Watson"))
                .andExpect(jsonPath("$.specialization").value("FAMILY_DOCTOR"))
                .andExpect(jsonPath("$.timeSlotId").value(1L))
                .andExpect(jsonPath("$.date").value("2030-01-01"))
                .andExpect(jsonPath("$.startTime").value("08:00"))
                .andExpect(jsonPath("$.endTime").value("09:00"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.comment").value("Test comment"));

        verify(appointmentService,times(1)).getAppointmentById(1L);
        verify(appointmentMapper,times(1)).toResponse(appointment);
    }
}

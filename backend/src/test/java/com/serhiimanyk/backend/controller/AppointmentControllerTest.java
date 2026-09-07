package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.AppointmentCreateRequest;
import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.exception.AppointmentNotFoundException;
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
import java.util.ArrayList;
import java.util.List;

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

        verify(appointmentService, times(1)).createAppointment(any(AppointmentCreateRequest.class));
        verify(appointmentMapper, times(1)).toResponse(appointment);
    }

    @Test
    public void getAppointmentById_shouldReturnAppointmentSuccessfully() throws Exception {

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

        verify(appointmentService, times(1)).getAppointmentById(1L);
        verify(appointmentMapper, times(1)).toResponse(appointment);
    }

    @Test
    public void getAppointmentById_shouldReturn404WhenAppointmentNotFound() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenThrow(new AppointmentNotFoundException("Appointment not found"));

        mockMvc.perform(
                        get("/api/appointments/1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Appointment not found"));

        verify(appointmentService, times(1)).getAppointmentById(1L);
        verify(appointmentMapper, never()).toResponse(any());
    }

    @Test
    public void getAppointmentsByPatientId_shouldReturnAppointmentsSuccessfully() throws Exception {

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setDoctor(doctor);
        timeSlot2.setId(2L);
        timeSlot2.setDate(LocalDate.of(2030, 1, 1));
        timeSlot2.setStartTime(LocalTime.of(9, 0));
        timeSlot2.setEndTime(LocalTime.of(10, 0));

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDoctor(doctor);
        appointment2.setPatient(patient);
        appointment2.setTimeSlot(timeSlot2);
        appointment2.setStatus(AppointmentStatus.CREATED);
        appointment2.setComment("Test comment");

        List<Appointment> appointments = List.of(appointment2, appointment);

        List<AppointmentResponse> appointmentResponses = List.of(
                new AppointmentResponse(
                        appointment2.getId(),

                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName(),

                        doctor.getId(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getSpecialization(),

                        timeSlot2.getId(),
                        timeSlot2.getDate(),
                        timeSlot2.getStartTime(),
                        timeSlot2.getEndTime(),

                        appointment2.getStatus(),
                        appointment2.getComment()),

                appointmentResponse);

        when(appointmentService.getAppointmentsByPatientId(patient.getId())).thenReturn(appointments);
        when(appointmentMapper.toResponseList(appointments)).thenReturn(appointmentResponses);
        mockMvc.perform(
                        get("/api/appointments/patient/" + patient.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].patientId").value(1L))
                .andExpect(jsonPath("$[0].patientFirstName").value("John"))
                .andExpect(jsonPath("$[0].patientLastName").value("Doe"))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].doctorFirstName").value("Doctor"))
                .andExpect(jsonPath("$[0].doctorLastName").value("Watson"))
                .andExpect(jsonPath("$[0].specialization").value("FAMILY_DOCTOR"))
                .andExpect(jsonPath("$[0].timeSlotId").value(2L))
                .andExpect(jsonPath("$[0].date").value("2030-01-01"))
                .andExpect(jsonPath("$[0].startTime").value("09:00"))
                .andExpect(jsonPath("$[0].endTime").value("10:00"))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[0].comment").value("Test comment"))

                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].patientId").value(1L))
                .andExpect(jsonPath("$[1].patientFirstName").value("John"))
                .andExpect(jsonPath("$[1].patientLastName").value("Doe"))
                .andExpect(jsonPath("$[1].doctorId").value(1L))
                .andExpect(jsonPath("$[1].doctorFirstName").value("Doctor"))
                .andExpect(jsonPath("$[1].doctorLastName").value("Watson"))
                .andExpect(jsonPath("$[1].specialization").value("FAMILY_DOCTOR"))
                .andExpect(jsonPath("$[1].timeSlotId").value(1L))
                .andExpect(jsonPath("$[1].date").value("2030-01-01"))
                .andExpect(jsonPath("$[1].startTime").value("08:00"))
                .andExpect(jsonPath("$[1].endTime").value("09:00"))
                .andExpect(jsonPath("$[1].status").value("CREATED"))
                .andExpect(jsonPath("$[1].comment").value("Test comment"));

        verify(appointmentService, times(1)).getAppointmentsByPatientId(1L);
        verify(appointmentMapper, times(1)).toResponseList(appointments);
    }

    @Test
    public void getAppointmentsByPatientId_shouldReturnEmptyList() throws Exception {

        List<Appointment> appointments = List.of();
        List<AppointmentResponse> appointmentResponses = List.of();

        when(appointmentService.getAppointmentsByPatientId(1L)).thenReturn(appointments);
        when(appointmentMapper.toResponseList(appointments)).thenReturn(appointmentResponses);

        mockMvc.perform(
                        get("/api/appointments/patient/" + patient.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(appointmentService, times(1)).getAppointmentsByPatientId(1L);
        verify(appointmentMapper, times(1)).toResponseList(appointments);
    }
}

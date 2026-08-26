package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.handler.GlobalExceptionHandler;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {

    private MockMvc mockMvc;
    private Doctor doctor;

    @InjectMocks
    DoctorController doctorController;

    @Mock
    DoctorService doctorService;

    @Mock
    DoctorMapper doctorMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(doctorController)
                .setControllerAdvice(GlobalExceptionHandler.class)
        .build();

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("firstName");
        doctor.setLastName("lastName");
        doctor.setEmail("doctor@test.com");
        doctor.setSpecialization(Specialization.DENTIST);
    }

    @Test
    public void createDoctor_shouldCreateDoctorSuccessfully() throws Exception {

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setFirstName("firstName");
        doctorRequest.setLastName("lastName");
        doctorRequest.setPhoneNumber("1234567890");
        doctorRequest.setEmail("doctor@test.com");
        doctorRequest.setPassword("password");
        doctorRequest.setSpecialization(Specialization.DENTIST);

        DoctorResponse doctorResponse = new DoctorResponse(
                doctor.getId(),
                doctorRequest.getFirstName(),
                doctorRequest.getLastName(),
                doctorRequest.getSpecialization());

        when(doctorMapper.toDoctor(any(DoctorRequest.class))).thenReturn(doctor);
        when(doctorMapper.toDoctorResponse(doctor)).thenReturn(doctorResponse);
        when(doctorService.createDoctor(doctor)).thenReturn(doctor);

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "firstName": "firstName",
                                          "lastName": "lastName",
                                          "phoneNumber": "1234567890",
                                          "email": "doctor@test.com",
                                          "password": "password",
                                          "specialization": "DENTIST"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/doctors/1"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.specialization").value("DENTIST"))
                .andExpect(jsonPath("$.id").value(1));

        verify(doctorMapper, times(1)).toDoctor(any(DoctorRequest.class));
        verify(doctorService, times(1)).createDoctor(doctor);
        verify(doctorMapper, times(1)).toDoctorResponse(doctor);
    }

    @Test
    public void getAllDoctors_shouldReturnDoctorsSuccessfully() throws Exception {

        Doctor doctor1 = new Doctor();
        doctor1.setId(2L);
        doctor1.setFirstName("firstName1");
        doctor1.setLastName("lastName1");
        doctor1.setSpecialization(Specialization.CARDIOLOGIST);

        List<Doctor> doctorList = List.of(doctor1, doctor);

        List<DoctorResponse> doctorResponseList = List.of(
                new DoctorResponse(
                        doctor1.getId(),
                        doctor1.getFirstName(),
                        doctor1.getLastName(),
                        doctor1.getSpecialization()
                ),
                new DoctorResponse(
                        doctor.getId(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getSpecialization()
                )
        );

        when(doctorService.getAllDoctors()).thenReturn(doctorList);
        when(doctorMapper.toDoctorsResponseList(doctorList)).thenReturn(doctorResponseList);

        mockMvc.perform(
                        get("/api/doctors")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("firstName1"))
                .andExpect(jsonPath("$[0].lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$[1].firstName").value("firstName"))
                .andExpect(jsonPath("$[1].lastName").value("lastName"))
                .andExpect(jsonPath("$[1].specialization").value("DENTIST"));

        verify(doctorMapper, times(1)).toDoctorsResponseList(doctorList);
        verify(doctorService, times(1)).getAllDoctors();
    }

    @Test
    public void getAllDoctors_shouldReturnEmptyListWhenNoDoctorsFound() throws Exception {

        List<Doctor> doctorList = List.of();
        List<DoctorResponse> doctorResponseList = List.of();

        when(doctorService.getAllDoctors()).thenReturn(doctorList);
        when(doctorMapper.toDoctorsResponseList(doctorList)).thenReturn(doctorResponseList);

        mockMvc.perform(
                        get("/api/doctors")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(doctorService, times(1)).getAllDoctors();
        verify(doctorMapper, times(1)).toDoctorsResponseList(doctorList);
    }

    @Test
    public void getDoctorById_shouldReturnDoctorSuccessfully() throws Exception {

        DoctorResponse doctorResponse = new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization());

        when(doctorService.getDoctorById(doctor.getId())).thenReturn(doctor);
        when(doctorMapper.toDoctorResponse(doctor)).thenReturn(doctorResponse);

        mockMvc.perform(
                        get("/api/doctors/" + doctor.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.specialization").value("DENTIST"))
                .andExpect(jsonPath("$.id").value(1));

        verify(doctorMapper, times(1)).toDoctorResponse(doctor);
        verify(doctorService, times(1)).getDoctorById(doctor.getId());
    }

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfully() throws Exception {

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(doctor.getId());
        updatedDoctor.setFirstName("updatedFirstName");
        updatedDoctor.setLastName("updatedLastName");
        updatedDoctor.setEmail(doctor.getEmail());
        updatedDoctor.setPhoneNumber(doctor.getPhoneNumber());
        updatedDoctor.setPassword(doctor.getPassword());
        updatedDoctor.setSpecialization(Specialization.CARDIOLOGIST);

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setFirstName(updatedDoctor.getFirstName());
        doctorRequest.setLastName(updatedDoctor.getLastName());
        doctorRequest.setEmail(updatedDoctor.getEmail());
        doctorRequest.setPhoneNumber(updatedDoctor.getPhoneNumber());
        doctorRequest.setPassword(updatedDoctor.getPassword());
        doctorRequest.setSpecialization(updatedDoctor.getSpecialization());

        DoctorResponse updatedDoctorResponse = new DoctorResponse(
                updatedDoctor.getId(),
                updatedDoctor.getFirstName(),
                updatedDoctor.getLastName(),
                updatedDoctor.getSpecialization()
        );

        when(doctorService.updateDoctor(any(DoctorRequest.class), eq(doctor.getId()))).thenReturn(updatedDoctor);
        when(doctorMapper.toDoctorResponse(updatedDoctor)).thenReturn(updatedDoctorResponse);

        mockMvc.perform(
                        put("/api/doctors/" + doctor.getId())

                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "firstName": "updatedFirstName",
                                          "lastName": "updatedLastName",
                                          "phoneNumber": "1234567890",
                                          "email": "doctor@test.com",
                                          "password": "password",
                                          "specialization": "CARDIOLOGIST"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("updatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("updatedLastName"))
                .andExpect(jsonPath("$.specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.id").value(1));

        verify(doctorMapper, times(1)).toDoctorResponse(updatedDoctor);
        verify(doctorService, times(1)).updateDoctor(any(DoctorRequest.class), eq(doctor.getId()));
    }

    @Test
    public void deleteDoctorById_shouldDeleteDoctorSuccessfully() throws Exception {

        mockMvc.perform(
                delete("/api/doctors/" + doctor.getId())
        )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(doctorService, times(1)).deleteDoctorById(doctor.getId());
    }

    @Test
    public void getDoctorById_shouldReturn404WhenDoctorNotFound() throws Exception {

        when(doctorService.getDoctorById(doctor.getId())).thenThrow(new DoctorNotFoundException(
                "Doctor with id " + doctor.getId() + " not found"
        ));

        mockMvc.perform(
                get("/api/doctors/" + doctor.getId())
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id " + doctor.getId() + " not found"));

        verify(doctorService, times(1)).getDoctorById(doctor.getId());
    }

    @Test
    public void createDoctor_shouldReturn409WhenEmailAlreadyExists() throws Exception {

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setFirstName("firstName");
        doctorRequest.setLastName("lastName");
        doctorRequest.setPhoneNumber("1234567890");
        doctorRequest.setEmail("doctor@test.com");
        doctorRequest.setPassword("password");
        doctorRequest.setSpecialization(Specialization.DENTIST);

        when(doctorMapper.toDoctor(any(DoctorRequest.class))).thenReturn(doctor);
        when(doctorService.createDoctor(doctor)).thenThrow(new EmailAlreadyExistsException(
                "Doctor with email " + doctor.getEmail() + " already exists"
        ));

        mockMvc.perform(
                post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                        {
                                          "firstName": "firstName",
                                          "lastName": "lastName",
                                          "phoneNumber": "1234567890",
                                          "email": "doctor@test.com",
                                          "password": "password",
                                          "specialization": "DENTIST"
                                        }
                                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor with email " + doctor.getEmail() + " already exists"));

        verify(doctorService, times(1)).createDoctor(doctor);
        verify(doctorMapper, times(1)).toDoctor(any(DoctorRequest.class));
    }
}

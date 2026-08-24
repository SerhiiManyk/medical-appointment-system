package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("doctor@test.com");
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
    public void getAllDoctors_shouldReturnDoctorsSuccessfully() {
    }

    @Test
    public void getAllDoctors_shouldReturnEmptyListWhenNoDoctorsFound() {
    }

    @Test
    public void getDoctorById_shouldReturnDoctorSuccessfully() {
    }

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfully() {
    }

    @Test
    public void deleteDoctorById_shouldDeleteDoctorSuccessfully() {
    }
}

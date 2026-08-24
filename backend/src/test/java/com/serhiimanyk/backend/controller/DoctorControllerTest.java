package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    public void createDoctor_shouldCreateDoctorSuccessfully(){

        DoctorRequest  doctorRequest = new DoctorRequest();
        doctorRequest.setFirstName("firstName");
        doctorRequest.setLastName("lastName");
        doctorRequest.setPhoneNumber("1234567890");
        doctorRequest.setEmail("doctor@test.com");
        doctorRequest.setPassword("password");
        doctorRequest.setSpecialization(Specialization.DENTIST);
    }

    @Test
    public void getAllDoctors_shouldReturnDoctorsSuccessfully(){}

    @Test
    public void getAllDoctors_shouldReturnEmptyListWhenNoDoctorsFound(){}

    @Test
    public void getDoctorById_shouldReturnDoctorSuccessfully(){}

    @Test
    public void updateDoctor_shouldUpdateDoctorSuccessfully(){}

    @Test
    public void deleteDoctorById_shouldDeleteDoctorSuccessfully(){}
}

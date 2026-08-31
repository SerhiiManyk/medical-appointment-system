package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.PatientRequest;
import com.serhiimanyk.backend.dto.response.PatientResponse;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.enums.Gender;
import com.serhiimanyk.backend.handler.GlobalExceptionHandler;
import com.serhiimanyk.backend.mapper.PatientMapper;
import com.serhiimanyk.backend.service.PatientService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    private MockMvc mockMvc;
    private Patient patient;

    @InjectMocks
    PatientController patientController;

    @Mock
    PatientService patientService;

    @Mock
    PatientMapper patientMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(patientController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("patient@mail.com");
        patient.setGender(Gender.MALE);
        patient.setDateOfBirth(LocalDate.of(1989, 7, 7));
    }

    @Test
    public void createPatient_shouldCreatePatientSuccessfully() throws Exception {

        PatientResponse patientResponse = new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getGender(),
                patient.getDateOfBirth()
        );

        when(patientMapper.toPatient(any(PatientRequest.class))).thenReturn(patient);
        when(patientMapper.toPatientResponse(patient)).thenReturn(patientResponse);
        when(patientService.createPatient(patient)).thenReturn(patient);

        mockMvc.perform(
                post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                   "firstName": "John",
                                   "lastName": "Doe",
                                   "phoneNumber": "1234567890",
                                   "email": "patient@mail.com",
                                   "password": "password",
                                   "gender": "MALE",
                                   "dateOfBirth": "1989-07-07"
                                 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.dateOfBirth").value("1989-07-07"))
                .andExpect(jsonPath("$.id").value(1));

        verify(patientMapper,times(1)).toPatient(any(PatientRequest.class));
        verify(patientMapper,times(1)).toPatientResponse(patient);
        verify(patientService,times(1)).createPatient(patient);
    }
}

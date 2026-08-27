package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.handler.GlobalExceptionHandler;
import com.serhiimanyk.backend.mapper.PatientMapper;
import com.serhiimanyk.backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    }
}

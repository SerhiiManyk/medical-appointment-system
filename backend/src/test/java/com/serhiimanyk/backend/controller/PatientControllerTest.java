package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.PatientRequest;
import com.serhiimanyk.backend.dto.response.PatientResponse;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.enums.Gender;
import com.serhiimanyk.backend.exception.PatientNotFoundException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        verify(patientMapper, times(1)).toPatient(any(PatientRequest.class));
        verify(patientMapper, times(1)).toPatientResponse(patient);
        verify(patientService, times(1)).createPatient(patient);
    }

    @Test
    public void getAllPatients_shouldReturnPatientsSuccessfully() throws Exception {
        Patient patient1 = new Patient();
        patient1.setId(2L);
        patient1.setFirstName("Adam");
        patient1.setLastName("Snow");
        patient1.setGender(Gender.MALE);
        patient1.setDateOfBirth(LocalDate.of(1995, 3, 8));

        List<Patient> patientList = List.of(patient, patient1);

        List<PatientResponse> patientResponseList = List.of(
                new PatientResponse(
                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getGender(),
                        patient.getDateOfBirth()),

                new PatientResponse(
                        patient1.getId(),
                        patient1.getFirstName(),
                        patient1.getLastName(),
                        patient1.getGender(),
                        patient1.getDateOfBirth()
                )
        );

        when(patientService.getAllPatients()).thenReturn(patientList);
        when(patientMapper.toPatientResponseList(patientList)).thenReturn(patientResponseList);
        mockMvc.perform(
                        get("/api/patients")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].gender").value("MALE"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("1989-07-07"))
                .andExpect(jsonPath("$[1].firstName").value("Adam"))
                .andExpect(jsonPath("$[1].lastName").value("Snow"))
                .andExpect(jsonPath("$[1].gender").value("MALE"))
                .andExpect(jsonPath("$[1].dateOfBirth").value("1995-03-08"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(patientService, times(1)).getAllPatients();
        verify(patientMapper, times(1)).toPatientResponseList(patientList);
    }

    @Test
    public void getAllPatients_shouldReturnEmptyListWhenNoPatientsFound() throws Exception {

        List<Patient> patientList = List.of();
        List<PatientResponse> patientResponseList = List.of();

        when(patientService.getAllPatients()).thenReturn(patientList);
        when(patientMapper.toPatientResponseList(patientList)).thenReturn(patientResponseList);

        mockMvc.perform(
                        get("/api/patients")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(patientService, times(1)).getAllPatients();
        verify(patientMapper, times(1)).toPatientResponseList(patientList);
    }

    @Test
    public void getPatientById_shouldReturnPatientSuccessfully() throws Exception {

        PatientResponse patientResponse = new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getGender(),
                patient.getDateOfBirth()
        );

        when(patientService.getPatientById(patient.getId())).thenReturn(patient);
        when(patientMapper.toPatientResponse(patient)).thenReturn(patientResponse);

        mockMvc.perform(
                        get("/api/patients/" + patient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.dateOfBirth").value("1989-07-07"))
                .andExpect(jsonPath("$.id").value(1));

        verify(patientService, times(1)).getPatientById(patient.getId());
        verify(patientMapper, times(1)).toPatientResponse(patient);
    }

    @Test
    public void getPatientById_shouldReturn404WhenPatientNotFound() throws Exception {

        when(patientService.getPatientById(patient.getId())).thenThrow(new PatientNotFoundException(
                "Patient with id " + patient.getId() + " is not found"));

        mockMvc.perform(
                        get("/api/patients/" + patient.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with id " + patient.getId() + " is not found"));

        verify(patientService, times(1)).getPatientById(patient.getId());
        verify(patientMapper, never()).toPatientResponse(any());
    }

    @Test
    public void updatePatient_shouldUpdatePatientSuccessfully() throws Exception {

        Patient patientForUpdate = new Patient();
        patientForUpdate.setId(patient.getId());
        patientForUpdate.setFirstName("Anton");
        patientForUpdate.setLastName("Daniel");
        patientForUpdate.setPhoneNumber(patient.getPhoneNumber());
        patientForUpdate.setGender(patient.getGender());
        patientForUpdate.setDateOfBirth(patient.getDateOfBirth());
        patientForUpdate.setEmail(patient.getEmail());
        patientForUpdate.setPassword(patient.getPassword());

        PatientResponse patientResponse = new PatientResponse(
                patientForUpdate.getId(),
                patientForUpdate.getFirstName(),
                patientForUpdate.getLastName(),
                patientForUpdate.getGender(),
                patientForUpdate.getDateOfBirth()
        );

        when(patientService.updatePatient(any(PatientRequest.class), eq(patient.getId()))).thenReturn(patientForUpdate);
        when(patientMapper.toPatientResponse(patientForUpdate)).thenReturn(patientResponse);

        mockMvc.perform(put("/api/patients/" + patient.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Anton",
                        "lastName": "Daniel",
                        "phoneNumber": "1234567890",
                        "email": "patient@mail.com",
                        "password": "password",
                        "gender": "MALE",
                        "dateOfBirth": "1989-07-07"
                        }
                        """)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anton"))
                .andExpect(jsonPath("$.lastName").value("Daniel"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.dateOfBirth").value("1989-07-07"))
                .andExpect(jsonPath("$.id").value(1));

        verify(patientService, times(1)).updatePatient(any(PatientRequest.class), eq(patient.getId()));
        verify(patientMapper, times(1)).toPatientResponse(patientForUpdate);
    }

    @Test
    public void updatePatient_shouldReturn404WhenPatientNotFound() throws Exception {

        when(patientService.updatePatient(any(PatientRequest.class), eq(patient.getId()))).thenThrow(new PatientNotFoundException(
                "Patient with id " + patient.getId() + " is not found"));

        mockMvc.perform(put("/api/patients/" + patient.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Anton",
                        "lastName": "Daniel",
                        "phoneNumber": "1234567890",
                        "email": "patient@mail.com",
                        "password": "password",
                        "gender": "MALE",
                        "dateOfBirth": "1989-07-07"
                        }
                        """)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with id " + patient.getId() + " is not found"));

        verify(patientService, times(1)).updatePatient(any(PatientRequest.class), eq(patient.getId()));
        verify(patientMapper, never()).toPatientResponse(any());
    }

}

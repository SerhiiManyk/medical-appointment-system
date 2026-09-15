package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.PatientRequest;
import com.serhiimanyk.backend.dto.response.PatientResponse;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatientMapperTest {

    private PatientMapper patientMapper;
    private Patient patient;

    @BeforeEach
    public void setup(){
        patientMapper = new PatientMapper();

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("patient@test.email");
        patient.setPassword("12345");
        patient.setPhoneNumber("1234567890");
        patient.setDateOfBirth(LocalDate.of(1980, 1, 1));
        patient.setGender(Gender.MALE);
    }

    @Test
    public void toPatientResponse_shouldMapPatientToPatientResponse(){

        PatientResponse patientResponse = patientMapper.toPatientResponse(patient);

        assertEquals(patient.getId(), patientResponse.getId());
        assertEquals(patient.getFirstName(), patientResponse.getFirstName());
        assertEquals(patient.getLastName(), patientResponse.getLastName());
        assertEquals(patient.getGender(), patientResponse.getGender());
        assertEquals(patient.getDateOfBirth(), patientResponse.getDateOfBirth());
    }

    @Test
    public void toPatientsResponseList_shouldMapPatientsToPatientResponseList(){

       Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Anna");
        patient2.setLastName("Lee");
        patient2.setEmail("patient2@test.email");
        patient2.setPassword("1234567");
        patient2.setPhoneNumber("1234567890");
        patient2.setDateOfBirth(LocalDate.of(1999, 1, 1));
        patient2.setGender(Gender.FEMALE);

        List<Patient> patientList = List.of(patient, patient2);

        List<PatientResponse> patientResponseList = patientMapper.toPatientResponseList(patientList);

        assertEquals(2, patientResponseList.size());
        assertEquals(patientList.getFirst().getId(), patientResponseList.getFirst().getId());
        assertEquals(patientList.getFirst().getFirstName(), patientResponseList.getFirst().getFirstName());
        assertEquals(patientList.getFirst().getLastName(), patientResponseList.getFirst().getLastName());
        assertEquals(patientList.get(0).getGender(), patientResponseList.get(0).getGender());
        assertEquals(patientList.get(0).getDateOfBirth(), patientResponseList.get(0).getDateOfBirth());
        assertEquals(patientList.get(1).getId(), patientResponseList.get(1).getId());
        assertEquals(patientList.get(1).getFirstName(), patientResponseList.get(1).getFirstName());
        assertEquals(patientList.get(1).getLastName(), patientResponseList.get(1).getLastName());
        assertEquals(patientList.get(1).getGender(), patientResponseList.get(1).getGender());
        assertEquals(patientList.get(1).getDateOfBirth(), patientResponseList.get(1).getDateOfBirth());
    }

    @Test
    public void toPatient_shouldMapPatientRequestToPatient(){

        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setFirstName("John");
        patientRequest.setLastName("Doe");
        patientRequest.setEmail("patient@test.email");
        patientRequest.setPassword("12345");
        patientRequest.setPhoneNumber("1234567890");
        patientRequest.setDateOfBirth(LocalDate.of(1980, 1, 1));
        patientRequest.setGender(Gender.MALE);

        Patient resultPatient = patientMapper.toPatient(patientRequest);

        assertEquals(patient.getFirstName(), resultPatient.getFirstName());
        assertEquals(patient.getLastName(), resultPatient.getLastName());
        assertEquals(patient.getEmail(), resultPatient.getEmail());
        assertEquals(patient.getPassword(), resultPatient.getPassword());
        assertEquals(patient.getPhoneNumber(), resultPatient.getPhoneNumber());
        assertEquals(patient.getDateOfBirth(), resultPatient.getDateOfBirth());
        assertEquals(patient.getGender(), resultPatient.getGender());
    }

    @Test
    public void updatePatientFromRequest_shouldUpdatePatientFields(){

        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setFirstName("John");
        patientRequest.setLastName("Doe");
        patientRequest.setEmail("patientChange@test.email");
        patientRequest.setPassword("12345");
        patientRequest.setPhoneNumber("0987654321");
        patientRequest.setDateOfBirth(LocalDate.of(1980, 1, 1));
        patientRequest.setGender(Gender.MALE);

        patientMapper.updatePatientFromRequest(patientRequest, patient);

        assertEquals(patient.getFirstName(), patientRequest.getFirstName());
        assertEquals(patient.getLastName(), patientRequest.getLastName());
        assertEquals(patient.getEmail(), patientRequest.getEmail());
        assertEquals(patient.getPassword(), patientRequest.getPassword());
        assertEquals(patient.getPhoneNumber(), patientRequest.getPhoneNumber());
        assertEquals(patient.getDateOfBirth(), patientRequest.getDateOfBirth());
        assertEquals(patient.getGender(), patientRequest.getGender());
    }
}

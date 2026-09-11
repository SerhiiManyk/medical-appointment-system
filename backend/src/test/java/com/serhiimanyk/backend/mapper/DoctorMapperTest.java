package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoctorMapperTest {

    private Doctor doctor;
    private DoctorResponse doctorResponse;
    private DoctorMapper doctorMapper;

    @BeforeEach
    public void setup() {

        doctorMapper = new DoctorMapper();

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setPassword("12345");
        doctor.setEmail("doctor@test.mail");
        doctor.setPhoneNumber("1234567890");
        doctor.setSpecialization(Specialization.DERMATOLOGIST);
    }

    @Test
    public void toDoctorResponse_shouldMapDoctorToDoctorResponse() {

        doctorResponse = doctorMapper.toDoctorResponse(doctor);

        assertEquals(doctor.getId(), doctorResponse.getId());
        assertEquals(doctor.getFirstName(), doctorResponse.getFirstName());
        assertEquals(doctor.getLastName(), doctorResponse.getLastName());
        assertEquals(doctor.getSpecialization(), doctorResponse.getSpecialization());
    }
}

package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void toDoctorsResponseList_shouldMapDoctorsToDoctorResponseList() {

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setFirstName("Bax");
        doctor2.setLastName("Bani");
        doctor2.setSpecialization(Specialization.DERMATOLOGIST);

        List<Doctor> doctors = List.of(doctor, doctor2);

        List<DoctorResponse> doctorResponseList = doctorMapper.toDoctorsResponseList(doctors);

        assertEquals(2, doctorResponseList.size());
        assertEquals(doctors.get(0).getId(), doctorResponseList.get(0).getId());
        assertEquals(doctors.get(1).getId(), doctorResponseList.get(1).getId());
        assertEquals(doctors.get(0).getFirstName(), doctorResponseList.get(0).getFirstName());
        assertEquals(doctors.get(1).getFirstName(), doctorResponseList.get(1).getFirstName());
        assertEquals(doctors.get(0).getLastName(), doctorResponseList.get(0).getLastName());
        assertEquals(doctors.get(1).getLastName(), doctorResponseList.get(1).getLastName());
        assertEquals(doctors.get(0).getSpecialization(), doctorResponseList.get(0).getSpecialization());
        assertEquals(doctors.get(1).getSpecialization(), doctorResponseList.get(1).getSpecialization());
    }

    @Test
    public void toDoctor_shouldMapDoctorRequestToDoctor(){

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setFirstName("John");
        doctorRequest.setLastName("Doe");
        doctorRequest.setPassword("12345");
        doctorRequest.setEmail("doctor@test.mail");
        doctorRequest.setPhoneNumber("1234567890");
        doctorRequest.setSpecialization(Specialization.DERMATOLOGIST);

        Doctor resultDoctor = doctorMapper.toDoctor(doctorRequest);

        assertEquals(doctorRequest.getFirstName(), resultDoctor.getFirstName());
        assertEquals(doctorRequest.getLastName(), resultDoctor.getLastName());
        assertEquals(doctorRequest.getSpecialization(), resultDoctor.getSpecialization());
        assertEquals(doctorRequest.getPassword(), resultDoctor.getPassword());
        assertEquals(doctorRequest.getEmail(), resultDoctor.getEmail());
        assertEquals(doctorRequest.getPhoneNumber(), resultDoctor.getPhoneNumber());
    }

    @Test
    public void updateDoctorFromRequest_shouldUpdateDoctorFields(){

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setFirstName("John");
        doctorRequest.setLastName("Doe");
        doctorRequest.setPassword("12345");
        doctorRequest.setEmail("doctorChange@test.mail");
        doctorRequest.setPhoneNumber("1234567890");
        doctorRequest.setSpecialization(Specialization.DERMATOLOGIST);

        doctorMapper.updateDoctorFromRequest(doctorRequest, doctor);

        assertEquals(doctor.getFirstName(), doctorRequest.getFirstName());
        assertEquals(doctor.getLastName(), doctorRequest.getLastName());
        assertEquals(doctor.getPassword(), doctorRequest.getPassword());
        assertEquals(doctor.getEmail(), doctorRequest.getEmail());
        assertEquals(doctor.getPhoneNumber(), doctorRequest.getPhoneNumber());
        assertEquals(doctor.getSpecialization(), doctorRequest.getSpecialization());
    }
}

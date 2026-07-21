package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.dto.response.DoctorResponse;
import com.serhiimanyk.backend.entity.Doctor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorMapper {

    public DoctorResponse toDoctorResponse(Doctor doctor) {

        return DoctorResponse.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .build();
    }

    public List<DoctorResponse> toDoctorsResponseList(
            List<Doctor> doctors
    ) {
        return doctors.stream()
                .map(this::toDoctorResponse)
                .toList();
    }

    public Doctor toDoctor(DoctorRequest doctorRequest) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(doctorRequest.getFirstName());
        doctor.setLastName(doctorRequest.getLastName());
        doctor.setSpecialization(doctorRequest.getSpecialization());
        doctor.setEmail(doctorRequest.getEmail());
        doctor.setPassword(doctorRequest.getPassword());
        doctor.setPhoneNumber(doctorRequest.getPhoneNumber());

        return doctor;
    }
}

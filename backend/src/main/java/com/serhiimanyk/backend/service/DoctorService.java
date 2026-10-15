package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;

import java.util.List;

public interface DoctorService {

    Doctor getDoctorByEmail(String email);

    Doctor getDoctorById(Long id);

    List<Doctor> getBySpecialization(Specialization specialization);

    List<Doctor> getAllDoctors ();

    Doctor createDoctor(Doctor doctor);

    Doctor updateDoctor(DoctorRequest request, Long id);

    void deleteDoctorById(Long id);

}

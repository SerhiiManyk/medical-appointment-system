package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;

import java.util.List;

public interface DoctorService {

    Doctor getDoctorByEmail(String email);

    Doctor getDoctorById(Long id);

    List<Doctor> getBySpecialization(Specialization specialization);

    void checkEmailUnique(String email);

    List<Doctor> getAllDoctors ();

    Doctor createDoctor(Doctor doctor);

    Doctor updateDoctor(Doctor doctor);

    void deleteDoctorById(Long id);

}

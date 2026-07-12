package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class DoctorServiceImpl implements DoctorService {


    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }


    @Override
    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() ->new DoctorNotFoundException("Doctor with email " + email + " is not found"));
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return null;
    }

    @Override
    public List<Doctor> getBySpecialization(Specialization specialization) {
        return List.of();
    }

    @Override
    public void checkEmailUnique(String email) {

    }

    @Override
    public List<Doctor> getAllDoctors() {
        return List.of();
    }

    @Override
    public Doctor createDoctor(Doctor doctor) {
        return null;
    }

    @Override
    public Doctor updateDoctor(Doctor doctor) {
        return null;
    }

    @Override
    public void deleteDoctorById(Long id) {

    }
}

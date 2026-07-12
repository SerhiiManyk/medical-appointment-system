package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.service.DoctorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {


    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }


    @Override
    public Doctor getDoctorByEmail(String email) {

        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with email " + email + " is not found"));
    }

    @Override
    public Doctor getDoctorById(Long id) {

        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + id + " not found"));
    }

    @Override
    public List<Doctor> getBySpecialization(Specialization specialization) {

        return doctorRepository.findBySpecialization(specialization);
    }

    @Override
    public List<Doctor> getAllDoctors() {

        return doctorRepository.findAll();
    }

    @Override
    public Doctor createDoctor(Doctor doctor) {

        if(doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new EmailAlreadyExistsException("Doctor with email " + doctor.getEmail() + " already exists");
        }
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Doctor doctor) {

        Doctor doctorToUpdate = getDoctorById(doctor.getId());

        doctorToUpdate.setFirstName(doctor.getFirstName());
        doctorToUpdate.setLastName(doctor.getLastName());
        doctorToUpdate.setSpecialization(doctor.getSpecialization());
        doctorToUpdate.setPassword(doctor.getPassword());
        doctorToUpdate.setPhoneNumber(doctor.getPhoneNumber());

        if (!doctorToUpdate.getEmail().equals(doctor.getEmail())) {

            if (doctorRepository.existsByEmailAndIdNot(
                    doctor.getEmail(),
                    doctor.getId()
            )) {
                throw new EmailAlreadyExistsException(
                        "Doctor with email " + doctor.getEmail() + " already exists"
                );
            }

            doctorToUpdate.setEmail(doctor.getEmail());
        }

        return doctorRepository.save(doctorToUpdate);
    }

    @Override
    public void deleteDoctorById(Long id) {

        Doctor doctor = getDoctorById(id);

        doctorRepository.delete(doctor);
    }
}

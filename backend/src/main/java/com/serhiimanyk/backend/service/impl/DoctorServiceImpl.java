package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.DoctorRequest;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.mapper.DoctorMapper;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

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

        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new EmailAlreadyExistsException("Doctor with email " + doctor.getEmail() + " already exists");
        }
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(DoctorRequest request, Long id) {

        Doctor doctorToUpdate = getDoctorById(id);

        if (!doctorToUpdate.getEmail().equals(request.getEmail())) {

            if (doctorRepository.existsByEmailAndIdNot(
                    request.getEmail(),
                    id
            )) {
                throw new EmailAlreadyExistsException(
                        "Doctor with email " + request.getEmail() + " already exists"
                );
            }
        }
        doctorMapper.updateDoctorFromRequest(request, doctorToUpdate);

        return doctorRepository.save(doctorToUpdate);
    }

    @Override
    public void deleteDoctorById(Long id) {

        Doctor doctor = getDoctorById(id);

        doctorRepository.delete(doctor);
    }
}

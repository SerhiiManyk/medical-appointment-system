package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    List<Doctor> findBySpecialization(Specialization specialization);

    boolean existsByEmail(String email);
}

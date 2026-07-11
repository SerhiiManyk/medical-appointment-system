package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}

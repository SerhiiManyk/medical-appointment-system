package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}

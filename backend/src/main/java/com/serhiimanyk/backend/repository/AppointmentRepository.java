package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long id);

    List<Appointment> findByDoctorId(Long id);

    boolean existsByTimeSlot_IdAndStatus(Long timeSlotId, AppointmentStatus status);
}

package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long id);

    List<Appointment> findByDoctorId(Long id);

    boolean existsByTimeSlot_IdAndStatus(Long timeSlotId, AppointmentStatus status);

    boolean existsByPatient_IdAndTimeSlot_DateAndTimeSlot_StartTimeAndTimeSlot_EndTimeAndStatusAndIdNot(
            Long userId,
            LocalDate date,
            LocalTime start,
            LocalTime endTime,
            AppointmentStatus status,
            Long appointmentId);

    long countByTimeSlot_IdAndStatus(Long timeSlotId, AppointmentStatus status);
}

package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    boolean existsByDoctorIdAndDateAndStartTimeAndEndTime (Long id, LocalDate date, LocalTime startTime, LocalTime endTime );

    List<TimeSlot> findAllByDoctorId(Long id);

    List<TimeSlot> findByDoctorIdAndStatus(Long doctorId, TimeSlotStatus status);

    List<TimeSlot> findAllTimeSlotsByDateAndStatus( LocalDate date, TimeSlotStatus status);
}

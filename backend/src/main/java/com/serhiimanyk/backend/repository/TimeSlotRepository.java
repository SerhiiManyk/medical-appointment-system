package com.serhiimanyk.backend.repository;

import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    boolean existsByDoctorIdAndDateAndStartTimeAndEndTime (Long id, LocalDate date, LocalTime startTime, LocalTime endTime );

    List<TimeSlot> findAllByDoctorId(Long id);

    List<TimeSlot> findByDoctorIdAndStatus(Long doctorId, TimeSlotStatus status);

    List<TimeSlot> findAllByDoctorIdAndDateAndStatus(Long doctorId, LocalDate date, TimeSlotStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.id = :timeSlotId")
    Optional<TimeSlot> findTimeSlotByIdWithLock(@Param("timeSlotId") Long timeSlotId);
}

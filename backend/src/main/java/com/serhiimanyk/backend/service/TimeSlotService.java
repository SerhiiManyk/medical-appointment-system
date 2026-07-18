package com.serhiimanyk.backend.service;

import com.serhiimanyk.backend.entity.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotService {

    TimeSlot createTimeSlot(Long doctorId,TimeSlot timeSlot);

    TimeSlot getTimeSlotById(Long id);

    List<TimeSlot> getAllTimeSlotsByDoctorId(Long doctorId);

    List<TimeSlot> getAvailableTimeSlotsByDoctorId(Long doctorId);

    List<TimeSlot> getAvailableTimeSlotsByDate(LocalDate date);

    void blockTimeSlot(Long timeSlotId);

    void deleteTimeSlot(Long id);

}

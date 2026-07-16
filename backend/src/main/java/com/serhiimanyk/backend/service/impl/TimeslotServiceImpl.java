package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.TimeSlotRepository;
import com.serhiimanyk.backend.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TimeslotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    private final DoctorRepository doctorRepository;

    @Override
    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        return null;
    }

    @Override
    public TimeSlot getTimeSlotById(Long id) {
        return null;
    }

    @Override
    public List<TimeSlot> getAllTimeSlotsByDoctorId(Long doctorId) {
        return List.of();
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlotsByDoctorId(Long doctorId) {
        return List.of();
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlotsByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public void blockTimeSlot(Long timeSlotId) {

    }

    @Override
    public void deleteTimeSlot(Long id) {

    }
}

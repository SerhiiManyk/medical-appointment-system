package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.TimeSlotStatus;
import com.serhiimanyk.backend.exception.DoctorNotFoundException;
import com.serhiimanyk.backend.exception.InvalidTimeSlotException;
import com.serhiimanyk.backend.exception.TimeslotNotFoundException;
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
    public TimeSlot createTimeSlot(Long doctorId, TimeSlot timeSlot) {

        LocalDate nowDate = LocalDate.now();

        if (timeSlot.getEndTime().isBefore(timeSlot.getStartTime())) {
            throw new InvalidTimeSlotException("Time slot end time is before time slot start time.");
        }
        if (timeSlot.getDate().isBefore(nowDate)) {
            throw new InvalidTimeSlotException("Time slot date is before time slot start date.");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found."));

        if (timeSlotRepository.existsByDoctorIdAndDateAndStartTimeAndEndTime(doctorId, timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime())) {
            throw new InvalidTimeSlotException("Time slot already exists.");
        }

        timeSlot.setDoctor(doctor);
        timeSlot.setStatus(TimeSlotStatus.FREE);

        return timeSlotRepository.save(timeSlot);
    }

    @Override
    public TimeSlot getTimeSlotById(Long id) {

        return timeSlotRepository.findById(id).orElseThrow(() -> new TimeslotNotFoundException("Timeslot not found."));
    }

    @Override
    public List<TimeSlot> getAllTimeSlotsByDoctorId(Long doctorId) {

        doctorRepository.findById(doctorId).orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + doctorId + " is not found."));
        return timeSlotRepository.findAllByDoctorId(doctorId);
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlotsByDoctorId(Long doctorId) {

        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor with id " + doctorId + " is not found."));

        return timeSlotRepository.findByDoctorIdAndStatus(
                doctorId,
                TimeSlotStatus.FREE
        );
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlotsByDate(LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new InvalidTimeSlotException("Date cannot be in the past.");
        }
       return timeSlotRepository.findAllTimeSlotsByDateAndStatus(date, TimeSlotStatus.FREE);
    }

    @Override
    public void blockTimeSlot(Long timeSlotId) {

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId).orElseThrow(() -> new TimeslotNotFoundException("Timeslot not found."));
        if (timeSlot.getStatus() == TimeSlotStatus.BLOCKED) {
            throw new InvalidTimeSlotException("Timeslot is already blocked.");
        } else if (timeSlot.getStatus() == TimeSlotStatus.BOOKED) {
            throw new InvalidTimeSlotException("Cannot block booked time slot.");
        } else if (timeSlot.getStatus() == TimeSlotStatus.FREE) {
            timeSlot.setStatus(TimeSlotStatus.BLOCKED);
        }

        timeSlotRepository.save(timeSlot);
    }

    @Override
    public void deleteTimeSlot(Long id) {

        TimeSlot timeSlot = timeSlotRepository.findById(id).orElseThrow(() -> new TimeslotNotFoundException("TimeSlot with id " + id + " is not found."));

        if (timeSlot.getStatus() == TimeSlotStatus.BOOKED) {
            throw new InvalidTimeSlotException("Cannot delete booked time slot.");
        }
        timeSlotRepository.delete(timeSlot);
    }
}

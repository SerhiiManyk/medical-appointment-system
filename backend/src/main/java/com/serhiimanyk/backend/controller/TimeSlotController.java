package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.TimeSlotRequest;
import com.serhiimanyk.backend.dto.response.TimeSlotResponse;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.mapper.TimeSlotMapper;
import com.serhiimanyk.backend.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;
    private final TimeSlotMapper timeSlotMapper;

    @PostMapping
    public ResponseEntity<TimeSlotResponse> createTimeSlot(@Valid @RequestBody TimeSlotRequest timeSlotRequest, @PathVariable Long doctorId) {

        TimeSlot timeSlot = timeSlotMapper.toTimeSlot(timeSlotRequest);

        TimeSlot resultTimeSlot = timeSlotService.createTimeSlot(doctorId, timeSlot);

        TimeSlotResponse response = timeSlotMapper.toTimeSlotResponse(resultTimeSlot);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resultTimeSlot.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getAllTimeSlotsByDoctorId(@PathVariable Long doctorId) {

        List<TimeSlot> timeSlotList = timeSlotService.getAllTimeSlotsByDoctorId(doctorId);

        List<TimeSlotResponse> resultList = timeSlotMapper.toTimeSlotResponseList(timeSlotList);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableTimeSlotsByDoctorId(@PathVariable Long doctorId) {

        List<TimeSlot> timeSlotList = timeSlotService.getAvailableTimeSlotsByDoctorId(doctorId);

        List<TimeSlotResponse> resultList = timeSlotMapper.toTimeSlotResponseList(timeSlotList);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{timeSlotId}")
    public ResponseEntity<TimeSlotResponse> getTimeSlotById(@PathVariable Long doctorId, @PathVariable Long timeSlotId) {

        TimeSlot timeSlot = timeSlotService.getTimeSlotById(doctorId, timeSlotId);

        TimeSlotResponse response = timeSlotMapper.toTimeSlotResponse(timeSlot);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/available/by-date")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableTimeSlotsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam LocalDate date) {

        List<TimeSlot> resultList = timeSlotService.getAvailableTimeSlotsByDateAndDoctorId(doctorId, date);

        List<TimeSlotResponse> response = timeSlotMapper.toTimeSlotResponseList(resultList);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{timeSlotId}")
    public ResponseEntity<Void> deleteTimeSlotById(@PathVariable Long doctorId, @PathVariable Long timeSlotId) {

        timeSlotService.deleteTimeSlot(doctorId, timeSlotId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{timeSlotId}/block")
    public ResponseEntity<TimeSlotResponse> blockTimeSlot(
            @PathVariable Long doctorId,
            @PathVariable Long timeSlotId) {

        TimeSlot result = timeSlotService.blockTimeSlot(doctorId, timeSlotId);

        TimeSlotResponse response = timeSlotMapper.toTimeSlotResponse(result);

        return ResponseEntity.ok(response);
    }
}

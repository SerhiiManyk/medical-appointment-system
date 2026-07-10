package com.serhiimanyk.backend.entity;

import com.serhiimanyk.backend.enums.TimeSlotStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "TIME_SLOT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIME_SLOT_ID")
    private Long id;

    @Column(name = "DATE", nullable = false)
    @NotNull
    private LocalDate date;

    @Column(name = "START_TIME", nullable = false)
    @NotNull
    private LocalTime startTime;

    @Column(name = "END_TIME", nullable = false)
    @NotNull
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCTOR_ID", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID")
    private Patient patient;

    @NotNull(message = "{NotNull.timeslot.status}")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private TimeSlotStatus status;


}

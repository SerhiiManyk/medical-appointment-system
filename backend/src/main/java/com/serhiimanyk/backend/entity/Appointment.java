package com.serhiimanyk.backend.entity;

import com.serhiimanyk.backend.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "APPOINTMENTS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPOINTMENT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCTOR_ID", nullable = false)
    @NotNull(message = "{NotNull.appointment.doctor}")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    @NotNull(message = "{NotNull.appointment.patient}")
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIME_SLOT_ID", nullable = false)
    @NotNull(message = "{NotNull.appointment.timeSlot}")
    private TimeSlot timeSlot;

    @NotNull(message = "{NotNull.appointment.status}")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AppointmentStatus status;

    @Column(name = "APPOINTMENT_COMMENT")
    private String comment;
}

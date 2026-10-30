package com.serhiimanyk.backend.mapper;

import com.serhiimanyk.backend.dto.response.AppointmentResponse;
import com.serhiimanyk.backend.entity.Appointment;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.entity.TimeSlot;
import com.serhiimanyk.backend.enums.AppointmentStatus;
import com.serhiimanyk.backend.enums.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppointmentMapperTest {

    private Appointment appointment;
    private AppointmentMapper appointmentMapper;
    private Doctor doctor;
    private Patient patient;
    private TimeSlot timeSlot;

    @BeforeEach
    public void setup(){

        appointmentMapper = new AppointmentMapper();

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Doctor");
        doctor.setLastName("Aibolit");
        doctor.setSpecialization(Specialization.FAMILY_DOCTOR);

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Patient");
        patient.setLastName("Crocodile");

        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setDate(LocalDate.of(2030, 1, 1));
        timeSlot.setStartTime(LocalTime.of(9,0));
        timeSlot.setEndTime(LocalTime.of(10,0));

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setComment("Test comment");
    }

    @Test
    public void toResponse_shouldMapAppointmentToAppointmentResponse(){

       AppointmentResponse appointmentResponse =  appointmentMapper.toResponse(appointment);

        assertEquals(appointment.getId(), appointmentResponse.getId());
        assertEquals(appointment.getDoctor().getId(), appointmentResponse.getDoctorId());
        assertEquals(appointment.getPatient().getId(), appointmentResponse.getPatientId());
        assertEquals(appointment.getPatient().getFirstName(), appointmentResponse.getPatientFirstName());
        assertEquals(appointment.getPatient().getLastName(), appointmentResponse.getPatientLastName());
        assertEquals(appointment.getDoctor().getFirstName(), appointmentResponse.getDoctorFirstName());
        assertEquals(appointment.getDoctor().getLastName(), appointmentResponse.getDoctorLastName());
        assertEquals(appointment.getDoctor().getSpecialization(),appointmentResponse.getSpecialization());
        assertEquals(appointment.getTimeSlot().getDate(),appointmentResponse.getDate());
        assertEquals(appointment.getTimeSlot().getStartTime(),appointmentResponse.getStartTime());
        assertEquals(appointment.getTimeSlot().getEndTime(),appointmentResponse.getEndTime());
        assertEquals(appointment.getStatus(), appointmentResponse.getStatus());
        assertEquals(appointment.getComment(), appointmentResponse.getComment());
    }

    @Test
    public void toResponseList_shouldMapAppointmentsToAppointmentResponseList(){

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDoctor(doctor);
        appointment2.setPatient(patient);
        appointment2.setTimeSlot(timeSlot);
        appointment2.setStatus(AppointmentStatus.CREATED);
        appointment2.setComment("Test comment2");

        List<Appointment> appointmentList = List.of(appointment2,appointment);

        List<AppointmentResponse> appointmentResponseList = appointmentMapper.toResponseList(appointmentList);

        assertEquals(2, appointmentResponseList.size());
        assertEquals(appointmentList.get(0).getId(), appointmentResponseList.get(0).getId());
        assertEquals(appointmentList.get(0).getDoctor().getId(), appointmentResponseList.get(0).getDoctorId());
        assertEquals(appointmentList.get(0).getPatient().getId(), appointmentResponseList.get(0).getPatientId());
        assertEquals(appointmentList.get(0).getPatient().getFirstName(), appointmentResponseList.get(0).getPatientFirstName());
        assertEquals(appointmentList.get(0).getPatient().getLastName(), appointmentResponseList.get(0).getPatientLastName());
        assertEquals(appointmentList.get(0).getDoctor().getFirstName(), appointmentResponseList.get(0).getDoctorFirstName());
        assertEquals(appointmentList.get(0).getDoctor().getLastName(), appointmentResponseList.get(0).getDoctorLastName());
        assertEquals(appointmentList.get(0).getTimeSlot().getDate(),appointmentResponseList.get(0).getDate());
        assertEquals(appointmentList.get(0).getTimeSlot().getStartTime(),appointmentResponseList.get(0).getStartTime());
        assertEquals(appointmentList.get(0).getTimeSlot().getEndTime(),appointmentResponseList.get(0).getEndTime());
        assertEquals(appointmentList.get(0).getStatus(), appointmentResponseList.get(0).getStatus());
        assertEquals(appointmentList.get(0).getComment(), appointmentResponseList.get(0).getComment());
        assertEquals(appointmentList.get(1).getId(), appointmentResponseList.get(1).getId());
        assertEquals(appointmentList.get(1).getDoctor().getId(), appointmentResponseList.get(1).getDoctorId());
        assertEquals(appointmentList.get(1).getPatient().getId(), appointmentResponseList.get(1).getPatientId());
        assertEquals(appointmentList.get(1).getPatient().getFirstName(), appointmentResponseList.get(1).getPatientFirstName());
        assertEquals(appointmentList.get(1).getPatient().getLastName(), appointmentResponseList.get(1).getPatientLastName());
        assertEquals(appointmentList.get(1).getDoctor().getFirstName(), appointmentResponseList.get(1).getDoctorFirstName());
        assertEquals(appointmentList.get(1).getDoctor().getLastName(), appointmentResponseList.get(1).getDoctorLastName());
        assertEquals(appointmentList.get(1).getTimeSlot().getDate(),appointmentResponseList.get(1).getDate());
        assertEquals(appointmentList.get(1).getTimeSlot().getStartTime(),appointmentResponseList.get(1).getStartTime());
        assertEquals(appointmentList.get(1).getTimeSlot().getEndTime(),appointmentResponseList.get(1).getEndTime());
        assertEquals(appointmentList.get(1).getStatus(), appointmentResponseList.get(1).getStatus());
        assertEquals(appointmentList.get(1).getComment(), appointmentResponseList.get(1).getComment());
    }

    @Test
    public void toAppointment_shouldMapEntitiesToAppointment(){

        Appointment resultAppointment = appointmentMapper.toAppointment(doctor, patient, timeSlot);

        assertEquals(doctor.getId(), resultAppointment.getDoctor().getId());
        assertEquals(patient.getId(), resultAppointment.getPatient().getId());
        assertEquals(timeSlot.getId(), resultAppointment.getTimeSlot().getId());
        assertEquals(AppointmentStatus.CREATED, resultAppointment.getStatus());
    }
}

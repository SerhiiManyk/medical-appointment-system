package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.response.AuthResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.service.DoctorService;
import com.serhiimanyk.backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final DoctorService doctorService;

    private final PatientService patientService;

    public String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User is not authenticated"
            );
        }

        return authentication.getName();
    }

    public AuthResponse getCurrentUser() {

        String email = getCurrentUserEmail();

        Doctor doctor = doctorService.getDoctorByEmail(email);
        if (doctor != null) {
            return new AuthResponse(
                    doctor.getId(),
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctor.getEmail(),
                    doctor.getRole()
            );
        } else {
            Patient patient = patientService.getPatientByEmail(email);
            if (patient != null) {
                return new AuthResponse(
                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getEmail(),
                        patient.getRole()
                );
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        }
    }
}

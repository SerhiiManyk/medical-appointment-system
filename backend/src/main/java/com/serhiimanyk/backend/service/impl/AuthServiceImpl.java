package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.LoginRequest;
import com.serhiimanyk.backend.dto.response.AuthResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.service.DoctorService;
import com.serhiimanyk.backend.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final DoctorService doctorService;

    private final PatientService patientService;

    private final AuthenticationManager authenticationManager;

    private final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository;

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

    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication =  authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        httpSessionSecurityContextRepository.saveContext(context,request,response);

        return getCurrentUser();
    }
}

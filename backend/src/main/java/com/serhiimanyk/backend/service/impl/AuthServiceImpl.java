package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.LoginRequest;
import com.serhiimanyk.backend.dto.response.AuthResponse;
import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.exception.InvalidCredentialsException;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.service.DoctorService;
import com.serhiimanyk.backend.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;
    
    private final AuthenticationManager authenticationManager;

    private final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository;

    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

    private final CsrfTokenRepository csrfTokenRepository;

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

        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {

            Doctor doc = doctor.get();
            return new AuthResponse(
                    doc.getId(),
                    doc.getFirstName(),
                    doc.getLastName(),
                    doc.getEmail(),
                    doc.getRole()
            );
        } else {
           Optional <Patient> patient = patientRepository.findByEmail(email);
            if (patient.isPresent()) {

                Patient pat = patient.get();
                return new AuthResponse(
                        pat.getId(),
                        pat.getFirstName(),
                        pat.getLastName(),
                        pat.getEmail(),
                        pat.getRole()
                );
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        }
    }

    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        sessionAuthenticationStrategy.onAuthentication(
                authentication, request, response
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        httpSessionSecurityContextRepository.saveContext(context, request, response);

        csrfTokenRepository.saveToken(null, request, response);

        return getCurrentUser();
    }
}

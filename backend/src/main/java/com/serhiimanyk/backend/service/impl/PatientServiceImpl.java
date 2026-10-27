package com.serhiimanyk.backend.service.impl;

import com.serhiimanyk.backend.dto.request.PatientUpdateRequest;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.enums.Role;
import com.serhiimanyk.backend.exception.EmailAlreadyExistsException;
import com.serhiimanyk.backend.exception.PatientNotFoundException;
import com.serhiimanyk.backend.mapper.PatientMapper;
import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email " + email + " is not found"));
    }

    @Override
    public Patient getPatientById(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            return patientRepository.findById(id)
                    .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " is not found"));
        }
        if (isCurrentPatient(id)) {
            return patientRepository.findById(id)
                    .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " is not found"));
        }
        throw new AccessDeniedException("Access Denied");
    }

    @Override
    public List<Patient> getAllPatients() {

        return patientRepository.findAll();
    }

    @Override
    public Patient createPatient(Patient patient) {

        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new EmailAlreadyExistsException("Patient with email " + patient.getEmail() + " already exists");
        }

        patient.setPassword(passwordEncoder.encode(patient.getPassword()));

        patient.setRole(Role.PATIENT);

        return patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(PatientUpdateRequest request, Long id) {

        java.util.Collection<?> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (!roles.contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            throw new AccessDeniedException("Only patients can update patient data");
        }

        Patient patientToUpdate = getPatientById(id);

        if (!patientToUpdate.getEmail().equals(request.getEmail())) {

            if (patientRepository.existsByEmailAndIdNot(
                    request.getEmail(),
                    id
            )) {
                throw new EmailAlreadyExistsException(
                        "Patient with email " + request.getEmail() + " already exists"
                );
            }
        }

        if (request.getPassword() != null && isCurrentPatient(id)) {
            patientToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        patientMapper.updatePatientFromRequest(request, patientToUpdate);

        return patientRepository.save(patientToUpdate);
    }

    @Override
    public void deletePatientById(Long id) {

        java.util.Collection<?> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (!roles.contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            throw new AccessDeniedException("Only patients can delete his/her patient data");
        }

        Patient patient = getPatientById(id);

        patientRepository.delete(patient);
    }

    public Patient getPatientFromAuthentication() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return patientRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Patient with email " + username + " not found"));
    }

    public boolean isCurrentPatient(Long patientId) {

        Patient authenticatedPatient = getPatientFromAuthentication();

        return authenticatedPatient.getId().equals(patientId);
    }

}

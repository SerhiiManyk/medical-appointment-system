package com.serhiimanyk.backend.security;

import com.serhiimanyk.backend.entity.Doctor;
import com.serhiimanyk.backend.entity.Patient;
import com.serhiimanyk.backend.repository.DoctorRepository;
import com.serhiimanyk.backend.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public UserDetails findUserDetailsByEmail(String email) throws UsernameNotFoundException {

        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {
            Doctor resultDoctor = doctor.get();
            return User.builder()
                    .username(email)
                    .password(resultDoctor.getPassword())
                    .roles(String.valueOf(resultDoctor.getRole()))
                    .build();
        } else {
            Patient patient = patientRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            return User.builder()
                    .username(patient.getEmail())
                    .password(patient.getPassword())
                    .roles(String.valueOf(patient.getRole()))
                    .build();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return findUserDetailsByEmail(username);
    }
}

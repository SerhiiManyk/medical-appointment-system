package com.serhiimanyk.backend.config;

import com.serhiimanyk.backend.repository.PatientRepository;
import com.serhiimanyk.backend.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PatientRepository patientRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(Customizer.withDefaults());
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests

                .requestMatchers(HttpMethod.POST, "/api/patients").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/patients").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.PUT, "/api/patients/**").hasRole("PATIENT")
                .requestMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("PATIENT")

                .requestMatchers(HttpMethod.POST, "/api/doctors/*/timeslots").hasRole("DOCTOR")

                .requestMatchers(HttpMethod.GET,
                        "/api/doctors/*/timeslots/available",
                        "/api/doctors/*/timeslots/available/by-date"
                ).hasAnyRole("DOCTOR", "PATIENT")

                .requestMatchers(HttpMethod.GET, "/api/doctors/*/timeslots/**").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/timeslots/*/block").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.DELETE, "/api/doctors/*/timeslots/*").hasRole("DOCTOR")

                .requestMatchers(HttpMethod.GET, "/api/doctors", "/api/doctors/**")
                .hasAnyRole("DOCTOR", "PATIENT")

                .requestMatchers(HttpMethod.POST, "/api/doctors").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("DOCTOR")

                .requestMatchers(HttpMethod.POST, "/api/appointments")
                .hasRole("PATIENT")

                .requestMatchers(HttpMethod.GET, "/api/appointments/patient/**")
                .hasAnyRole("PATIENT", "DOCTOR")

                .requestMatchers(HttpMethod.GET, "/api/appointments/doctor/**")
                .hasRole("DOCTOR")

                .requestMatchers(HttpMethod.GET, "/api/appointments/*")
                .hasAnyRole("PATIENT", "DOCTOR")

                .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel")
                .hasAnyRole("PATIENT", "DOCTOR")

                .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/complete")
                .hasRole("DOCTOR")

                .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/reschedule")
                .hasAnyRole("PATIENT", "DOCTOR")

                .requestMatchers(HttpMethod.DELETE, "/api/appointments/*")
                .hasRole("DOCTOR")

                .requestMatchers("/error").permitAll()

                .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(BCryptPasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}

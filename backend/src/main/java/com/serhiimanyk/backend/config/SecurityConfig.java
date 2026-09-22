package com.serhiimanyk.backend.config;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(Customizer.withDefaults());
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests

                .requestMatchers(HttpMethod.POST,"/api/patients").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patients").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.GET,"/api/patients/**").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.PUT,"/api/patients/**").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.DELETE,"/api/patients/**").hasRole("DOCTOR")

                .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/doctors/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/doctors").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("DOCTOR")

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

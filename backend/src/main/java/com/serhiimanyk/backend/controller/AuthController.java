package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.response.AuthResponse;
import com.serhiimanyk.backend.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getLoggedInUser() {
    return ResponseEntity.ok(authService.getCurrentUser());
    }
}

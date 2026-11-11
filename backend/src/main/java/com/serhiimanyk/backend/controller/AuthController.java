package com.serhiimanyk.backend.controller;

import com.serhiimanyk.backend.dto.request.LoginRequest;
import com.serhiimanyk.backend.dto.response.AuthResponse;
import com.serhiimanyk.backend.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getLoggedInUser() {
    return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(loginRequest,request,response));
    }
}

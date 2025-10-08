package com.finance.manager.controller;

import com.finance.manager.dto.AuthRequest;
import com.finance.manager.dto.AuthResponse;
import com.finance.manager.dto.SignupRequest;
import com.finance.manager.service.AuthService;
import jakarta.validation.Valid; // New import for validation
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // New import for HttpStatus
import org.springframework.http.ResponseEntity; // New import for ResponseEntity
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
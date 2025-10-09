package com.finance.manager.controller;

import com.finance.manager.dto.AuthRequest;
import com.finance.manager.dto.AuthResponse;
import com.finance.manager.dto.SignupRequest;
import com.finance.manager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Public endpoint for new user registration */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        // Returns 201 CREATED but the account is NOT yet active (verification pending)
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /** Public endpoint for user authentication */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /** Public endpoint for email verification (hit by the email link) */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        authService.verifyUser(token);
        // Success response
        return ResponseEntity.ok("Success! Your email has been verified. You can now log in.");
    }

    /** Public endpoint to resend the verification email */
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestParam("email") String email) {
        try {
            authService.resendVerificationEmail(email);
        } catch (IllegalArgumentException e) {
            // Log the failure (e.g., user not found, already verified)
            // but return 204 to the client for security reasons (don't expose if email exists)
            System.err.println("Resend failed (silent for security): " + e.getMessage());
        }

        // Return 204 NO CONTENT regardless of success/failure for security
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
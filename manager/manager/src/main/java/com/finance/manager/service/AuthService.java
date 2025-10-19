package com.finance.manager.service;

import com.finance.manager.dto.AuthRequest;
import com.finance.manager.dto.AuthResponse;
import com.finance.manager.dto.SignupRequest;
import com.finance.manager.entity.User;
import com.finance.manager.exception.UserAlreadyExistsException;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException; // Important import
import org.springframework.security.authentication.DisabledException; // Important import
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    private void generateAndSendNewToken(User user) {
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        user.setVerificationToken(verificationToken);
        user.setTokenExpiryDate(expiryDate);
        userRepository.save(user);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .isVerified(false)
                .build();

        userRepository.save(user);

        generateAndSendNewToken(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!user.getIsVerified()) {
            throw new DisabledException("Account not verified. Please check your email or request a new link.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtTokenProvider.generateToken(authentication);
        return new AuthResponse(token, "Bearer");
    }

    @Transactional
    public void verifyUser(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token."));

        if (user.getTokenExpiryDate() == null || user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired. Please request a new one.");
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Verification request processed. Check your inbox."));

        if (user.getIsVerified()) {
            throw new IllegalArgumentException("User is already verified.");
        }
        generateAndSendNewToken(user);
    }
}
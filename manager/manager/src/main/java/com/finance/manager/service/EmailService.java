package com.finance.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // For asynchronous sending
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value; // For getting the sender email

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async // Run in a separate thread for performance and resilience
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Confirm Your Account - Action Required");

        String emailContent = String.format(
                "Welcome! Please click the link below to verify your email address:\n\n" +
                        "%s\n\n" +
                        "This link will expire in 24 hours. If you did not sign up for this, please ignore this email.",
                verificationLink
        );

        message.setText(emailContent);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log this error to a monitoring system (e.g., Sentry, ELK)
            System.err.println("Async failure to send verification email to " + toEmail + ": " + e.getMessage());
            // In a robust system, this would trigger a retry mechanism (e.g., using a queue)
        }
    }
}

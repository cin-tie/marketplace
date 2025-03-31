package com.cintie.marketplace_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.repositories.UserRepository;
import com.cintie.marketplace_backend.services.EmailService;
import com.cintie.marketplace_backend.utils.TokenUtils;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class VerifyEmailController {
    private final TokenUtils tokenUtils;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        if(tokenUtils.isTokenExpired(token)){
            return ResponseEntity.badRequest().body("Verification link has expired");
        }

        UserEntity user = userRepository.findByEmailVerificationToken(token).orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        return ResponseEntity.ok().body("Email verified succesfully");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email already verified");
        }
        
        if (user.getEmailVerificationToken() == null) {
            user.setEmailVerificationToken(tokenUtils.generateTokenWithTimestamp());
            userRepository.save(user);
        }
        
        emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());
        return ResponseEntity.ok("Verification email resent");
    }
}

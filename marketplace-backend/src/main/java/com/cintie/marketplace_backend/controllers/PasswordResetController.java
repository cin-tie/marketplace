package com.cintie.marketplace_backend.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cintie.marketplace_backend.services.PasswordResetService;

@RestController
@RequestMapping("/auth/password-reset")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            passwordResetService.initiatePasswordReset(email);
            return ResponseEntity.ok().body("Password reset code sent to email");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

    @PostMapping("/validate-code")
    public ResponseEntity<?> validateCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            
            if (email == null || code == null) {
                return ResponseEntity.badRequest().body("Email and code are required");
            }
            
            boolean isValid = passwordResetService.validateResetCode(email, code);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Validation failed");
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            String newPassword = request.get("newPassword");
            
            if (email == null || code == null || newPassword == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }
            
            passwordResetService.resetPassword(email, code, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Password reset failed");
        }
    }
}
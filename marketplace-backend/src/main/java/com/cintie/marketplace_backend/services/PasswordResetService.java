package com.cintie.marketplace_backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cintie.marketplace_backend.entities.PasswordResetTokenEntity;
import com.cintie.marketplace_backend.repositories.PasswordResetTokenRepository;
import com.cintie.marketplace_backend.repositories.UserRepository;
import com.cintie.marketplace_backend.utils.ResetCodeUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetCodeUtils resetCodeUtils;


    @Transactional
    public void initiatePasswordReset(String email) {
        tokenRepository.deleteByEmail(email);
        
        String resetCode = resetCodeUtils.generateNumericCode();
        
        PasswordResetTokenEntity token = new PasswordResetTokenEntity(email, resetCode);
        tokenRepository.save(token);
        
        emailService.sendPasswordResetCode(email, resetCode);
    }

    @Transactional
    public boolean validateResetCode(String email, String code) {
        return tokenRepository.findByEmailAndCode(email, code)
            .map(token -> !token.isExpired())
            .orElse(false);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        if (validateResetCode(email, code)) {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                tokenRepository.deleteByEmail(email);
            });
        } else {
            throw new IllegalArgumentException("Invalid or expired reset code");
        }
    }
}
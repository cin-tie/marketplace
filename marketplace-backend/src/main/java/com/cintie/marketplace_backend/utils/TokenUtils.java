package com.cintie.marketplace_backend.utils;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class TokenUtils {
    
    private static final long DEFAULT_EXPIRATION_TIME = 86400; 

    public String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
    
    public String generateTokenWithTimestamp() {
        return UUID.randomUUID().toString() + "_" + Instant.now().getEpochSecond();
    }
    
    public boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("_");
            if (parts.length < 2) return false;
            
            long creationTime = Long.parseLong(parts[1]);
            long currentTime = Instant.now().getEpochSecond();
            
            return (currentTime - creationTime) > DEFAULT_EXPIRATION_TIME;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
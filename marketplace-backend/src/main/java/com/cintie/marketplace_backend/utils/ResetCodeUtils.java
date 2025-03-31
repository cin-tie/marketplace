package com.cintie.marketplace_backend.utils;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class ResetCodeUtils {
    private static final SecureRandom random = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    
    public String generateNumericCode(){
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for(int i = 0; i < CODE_LENGTH; i++){
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public String generateAlphanumericCode(int length){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(length);
        for(int i = 0; i < length; i++){
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}

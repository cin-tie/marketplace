package com.cintie.marketplace_backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.exceptions.ValidationException;
import com.cintie.marketplace_backend.repositories.UserRepository;
import com.cintie.marketplace_backend.services.EmailService;
import com.cintie.marketplace_backend.services.UserService;
import com.cintie.marketplace_backend.utils.TokenUtils;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/telegram")
@AllArgsConstructor
public class TelegramController {

    private UserRepository userRepository;
    private EmailService emailService;
    private TokenUtils tokenUtils;
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/verify")
    public ResponseEntity<?> verifyTelegram(@RequestBody TelegramVerifyRequest request) {

        UserEntity user = userRepository.findByTelegram(request.telegramUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setTelegramChatId(request.telegramChatId());
        user.setTelegramId(request.telegramId());
        user.setTelegramVerified(true);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerViaTelegram(@RequestBody TelegramRegisterReq request){
        try{
            request.validate();
        } catch(ValidationException e){
            return ResponseEntity.badRequest().body(new String[] {e.getMessage()});
        }

        UserEntity userEntity = UserEntity.builder().username(request.username).password(request.password).role("USER").email(request.email).telegram(request.telegram).enabled(true).isEmailVerified(false).emailVerificationToken(tokenUtils.generateTokenWithTimestamp()).isTelegramVerified(false).telegramChatId(null).telegramId(null).build();

        try{
            userEntity = userService.createUser(userEntity);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new String[] {e.getMessage()});
        }

        emailService.sendVerificationEmail(userEntity.getEmail(), userEntity.getEmailVerificationToken());

        return ResponseEntity.ok().body(new String("success"));
    }

    @GetMapping("/check-userId/{telegramId}")
    public ResponseEntity<?> checkUserExists(@PathVariable Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
            .map(user -> ResponseEntity.ok(new UserStatusResponse(
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.isTelegramVerified()
            )))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check-user/{telegram}")
    public ResponseEntity<?> checkUserExists(@PathVariable String telegram) {
        return userRepository.findByTelegram(telegram)
            .map(user -> ResponseEntity.ok(new UserStatusResponse(
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.isTelegramVerified()
            )))
            .orElse(ResponseEntity.notFound().build());
    }

    private record UserStatusResponse(
        String username,
        String email,
        boolean isEmailVerified,
        boolean isTelegramVerified
    ) {}

    private record TelegramRegisterReq(String username, String email, String telegram, String password){
        public void validate() throws ValidationException{
            if (username == null || username.isBlank()) {throw new ValidationException("Username is required");}
            if (username.length() < 6) {throw new ValidationException("Username must contain more than 6 characters");}
            if (username.length() > 256) {throw new ValidationException("Username must contain less than 256 characters");}
            if (!username.matches("^[a-zA-Z0-9_]+$")) {throw new ValidationException("Username must contain only letters, numbers or symbol underscore");}
        
            if(email == null || email.isBlank()){throw new ValidationException("Email is required");}
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {throw new ValidationException("Invalid email address");}

            if(telegram == null || telegram.isBlank()){throw new ValidationException("Telegram is required");}
            if (!telegram.matches("^@[a-zA-Z0-9_]{5,32}$")) {throw new ValidationException("Invalid telegram id");}

            if (password == null || password.isBlank()) {throw new ValidationException("Password is required");}
            if (password.length() < 8) {throw new ValidationException("Password must contain more than 8 characters");}
            if (password.length() > 256) {throw new ValidationException("Password must contain less than 256 characters");}
            if (!password.matches(".*[a-z].*")) {throw new ValidationException("Password must contain at least one lowercase letter");}
            if (!password.matches(".*[A-Z].*")) {throw new ValidationException("Password must contain at least one uppercase letter");}
            if (!password.matches(".*[0-9].*")) {throw new ValidationException("Password must contain at least one number");}
        }
    }

    private record TelegramVerifyRequest(String telegramUsername, Long telegramId, Long telegramChatId) {}
}
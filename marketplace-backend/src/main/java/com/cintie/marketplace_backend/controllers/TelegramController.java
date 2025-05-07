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
import com.cintie.marketplace_backend.repositories.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/telegram")
@AllArgsConstructor
public class TelegramController {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/verify")
    public ResponseEntity<?> verifyTelegram(@RequestBody TelegramVerifyRequest request) {

        log.info("\n\n\nReceived Telegram registration request: {}", request);

        UserEntity user = userRepository.findByTelegram(request.telegramUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setTelegramChatId(request.telegramChatId());
        user.setTelegramId(request.telegramId());
        user.setTelegramVerified(true);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{telegramUsername}")
    public ResponseEntity<TelegramStatusResponse> getTelegramStatus(@PathVariable String telegramUsername) {
        UserEntity user = userRepository.findByTelegram(telegramUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(new TelegramStatusResponse(
            user.getTelegram(),
            user.getTelegramId(),
            user.getTelegramChatId(),
            user.isTelegramVerified()
        ));
    }

    private record TelegramVerifyRequest(String telegramUsername, Long telegramId, Long telegramChatId) {}
    private record TelegramStatusResponse(String telegramUsername, Long telegramId, Long telegramChatId, boolean isVerified) {}
}
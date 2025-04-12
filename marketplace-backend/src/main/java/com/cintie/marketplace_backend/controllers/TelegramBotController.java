package com.cintie.marketplace_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.repositories.ProductRepository;
import com.cintie.marketplace_backend.repositories.UserRepository;
import com.cintie.marketplace_backend.services.ProductService;
import com.cintie.marketplace_backend.utils.TokenUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@AllArgsConstructor
@RequestMapping("/api/telegram")
public class TelegramBotController {
    private final UserRepository userRepository;
    private final TokenUtils tokenUtils;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyTelegram(@RequestBody TelegramVerificationRequest request) {
        UserEntity user = userRepository.findByTelegram(request.getTelegram())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setTelegramChatId(request.getChatId());
        userRepository.save(user);
        
        if (user.isTelegramVerified()) {
            return ResponseEntity.ok(new TelegramVerificationResponse(true, "Telegram already verified"));
        }
        
        user.setTelegramVerificationToken(tokenUtils.generateTokenWithTimestamp());
        userRepository.save(user);
        
        return ResponseEntity.ok(new TelegramVerificationResponse(true, user.getTelegramVerificationToken()));
    }

    
    @PostMapping("/link-account")
    public ResponseEntity<?> linkTelegramAccount(@RequestBody TelegramLinkRequest request) {
        UserEntity user = userRepository.findByTelegramVerificationToken(request.getVerificationToken())
        .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setTelegramChatId(request.getChatId());
        user.setTelegramVerified(true);
        user.setTelegramVerificationToken(null);
        userRepository.save(user);
        
        return ResponseEntity.ok(new TelegramLinkResponse(true, "Telegram account linked successfully"));
    }

    @GetMapping("/user-products")
    public List<ProductEntity> getUserProductsByTelegram(@RequestParam Long chatId) {
        UserEntity user = userRepository.findByTelegramChatId(chatId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            return productRepository.findByUser(user);
    }

    @PostMapping("/products")
    public ProductEntity createProductViaTelegram(
        @RequestBody ProductEntity product,
        @RequestParam Long chatId
        ) {
        UserEntity user = userRepository.findByTelegramChatId(chatId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            return productService.createProduct(product, user);
    }
    
    @Data
    private static class TelegramVerificationRequest {
        private String telegram;
        private Long chatId;
    }

    @Data
    private static class TelegramVerificationResponse {
        private boolean success;
        private String message;

        public TelegramVerificationResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @Data
    private static class TelegramLinkRequest {
        private String verificationToken;
        private Long chatId;
    }

    @Data
    private static class TelegramLinkResponse {
        private boolean success;
        private String message;

        public TelegramLinkResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
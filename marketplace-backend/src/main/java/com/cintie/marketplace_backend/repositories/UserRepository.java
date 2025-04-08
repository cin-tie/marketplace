package com.cintie.marketplace_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cintie.marketplace_backend.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>{
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailVerificationToken(String token);
    Optional<UserEntity> findByTelegram(String telegram);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailVerificationToken(String token);
    boolean existsByTelegram(String telegram);
}

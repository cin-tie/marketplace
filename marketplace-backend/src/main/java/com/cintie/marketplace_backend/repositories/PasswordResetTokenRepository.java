package com.cintie.marketplace_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cintie.marketplace_backend.entities.PasswordResetTokenEntity;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}
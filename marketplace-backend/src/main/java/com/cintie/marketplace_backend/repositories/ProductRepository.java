package com.cintie.marketplace_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findByUser(UserEntity user);
}

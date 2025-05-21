package com.cintie.marketplace_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cintie.marketplace_backend.entities.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, String>{
    Optional<ProductImage> findByIdAndProductId(String id, String productId);  
}

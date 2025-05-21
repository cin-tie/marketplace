package com.cintie.marketplace_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.entities.ProductEntity.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<ProductEntity, String>{
    List<ProductEntity> findByUser(UserEntity user);
    List<ProductEntity> findByCategory(String category);
    List<ProductEntity> findByStatus(ProductStatus status);

    @Query("SELECT p FROM ProductEntity p WHERE p.isAuction = true AND p.status = 'ACTIVE' AND p.auctionEndTime > CURRENT_TIMESTAMP")
    List<ProductEntity> findActiveAuctions();
    
    @Query("SELECT p FROM ProductEntity p WHERE p.isAuction = false AND p.status = 'ACTIVE'")
    List<ProductEntity> findActiveFixedPriceProducts();
    
    @Query("SELECT p FROM ProductEntity p WHERE p.name LIKE %:query% OR p.description LIKE %:query%")
    List<ProductEntity> searchProducts(@Param("query") String query);

    Optional<ProductEntity> findByIdAndStatus(String id, ProductEntity.ProductStatus status);
    List<ProductEntity> findByStatusAndAuctionEndTimeBefore(ProductStatus active, LocalDateTime now);
}

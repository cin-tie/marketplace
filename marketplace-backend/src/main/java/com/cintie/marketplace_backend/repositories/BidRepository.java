package com.cintie.marketplace_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cintie.marketplace_backend.entities.BidEntity;
import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;

import java.util.List;


public interface BidRepository extends JpaRepository<BidEntity, String>{
    List<BidEntity> findByProduct(ProductEntity product);    
    List<BidEntity> findByBidder(UserEntity bidder);
    List<BidEntity> findByProductOrderByAmountDesc(ProductEntity productEntity);
    List<BidEntity> findByProductOrderByCreatedAtDesc(ProductEntity productEntity);
}

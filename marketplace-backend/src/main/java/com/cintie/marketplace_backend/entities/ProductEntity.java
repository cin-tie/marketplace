package com.cintie.marketplace_backend.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private double startPrice;

    @Column
    private double buyNowPrice;

    @Column(nullable = false)
    private double currentPrice;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Builder.Default
    @Column(nullable = false)
    private boolean isAuction = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime auctionEndTime; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private UserEntity user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<ProductImage> images = new ArrayList<>(10);

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference
    private List<BidEntity> bids = new ArrayList<>();

    @Column(nullable = false)
    private String category;

    public enum ProductStatus{
        ACTIVE, SOLD, EXPIRED, DELETED
    }

    public void addImage(ProductImage image) {
        if (images.size() >= 10) {
            throw new IllegalStateException("Cannot add more than 10 images");
        }
        images.add(image);
        image.setProduct(this);
    }
}

package com.cintie.marketplace_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Lob
    @Column(columnDefinition = "BYTEA", length = 2_097_152)
    private byte[] imageData;

    @Column(length = 50)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private ProductEntity product;
}
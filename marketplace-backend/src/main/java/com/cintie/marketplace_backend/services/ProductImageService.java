package com.cintie.marketplace_backend.services;

import org.springframework.stereotype.Service;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.ProductImage;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.exceptions.ProductImageNotFoundException;
import com.cintie.marketplace_backend.exceptions.ProductNotFoundException;
import com.cintie.marketplace_backend.exceptions.UnauthorizedAccessException;
import com.cintie.marketplace_backend.repositories.ProductImageRepository;
import com.cintie.marketplace_backend.repositories.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductImage saveImage(ProductImage image, String productId, UserEntity user) throws Exception{
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to add images to this product");
        }
        image.setProduct(product);
        return productImageRepository.save(image);
    }

    public ProductImage getImage(String imageId, String productId) throws Exception{
        return productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ProductImageNotFoundException("Image not found"));
    }

    @Transactional
    public void deleteImage(String imageId, String productId, UserEntity user) throws Exception{
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to remove images from this product");
        }

        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ProductImageNotFoundException("Image not found"));

        productImageRepository.delete(image);
    }
}

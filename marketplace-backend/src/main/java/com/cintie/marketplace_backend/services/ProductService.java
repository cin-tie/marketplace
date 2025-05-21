package com.cintie.marketplace_backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.ProductImage;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.exceptions.ProductNotFoundException;
import com.cintie.marketplace_backend.exceptions.UnauthorizedAccessException;
import com.cintie.marketplace_backend.repositories.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductEntity createProduct(ProductEntity product, UserEntity user){
        product.setUser(user);
        product.setCurrentPrice(product.getStartPrice());
        return productRepository.save(product);
    }

    public ProductEntity updateProduct(String id, ProductEntity updatedProduct, UserEntity user) throws Exception{
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!existingProduct.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserEntity.UserRole.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("You are not authorized to update this product");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setBuyNowPrice(updatedProduct.getBuyNowPrice());
        
        if (existingProduct.isAuction()) {
            existingProduct.setAuctionEndTime(updatedProduct.getAuctionEndTime());
        }

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(String id, UserEntity user) throws Exception{
        ProductEntity product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserEntity.UserRole.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this product");
        }

        product.setStatus(ProductEntity.ProductStatus.DELETED);
        productRepository.save(product);
    }

    public ProductEntity getProductById(String id) throws Exception{
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductEntity> getProductsByUser(UserEntity user){
        return productRepository.findByUser(user);
    }

    public List<ProductEntity> searchProducts(String query) {
        return productRepository.searchProducts(query);
    }
    
    public List<ProductEntity> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public ProductEntity addImageToProduct(String productId, ProductImage image, UserEntity user)  throws Exception{
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to add images to this product");
        }

        product.addImage(image);
        return productRepository.save(product);
    }

    public void removeImageFromProduct(String productId, String imageId, UserEntity user) throws Exception{
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to remove images from this product");
        }

        product.getImages().removeIf(img -> img.getId().equals(imageId));
        productRepository.save(product);
    }

    @Transactional
    public void updateProductStatuses() {
        List<ProductEntity> expiredProducts = productRepository.findByStatusAndAuctionEndTimeBefore(
            ProductEntity.ProductStatus.ACTIVE, 
            LocalDateTime.now()
        );
        
        expiredProducts.forEach(product -> {
            if (product.getBids().isEmpty()) {
                product.setStatus(ProductEntity.ProductStatus.EXPIRED);
            } else {
                product.setStatus(ProductEntity.ProductStatus.SOLD);
            }
        });
        
        productRepository.saveAll(expiredProducts);
    }
}

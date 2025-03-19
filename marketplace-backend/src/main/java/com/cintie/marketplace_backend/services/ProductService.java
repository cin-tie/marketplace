package com.cintie.marketplace_backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.repositories.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;

    public ProductEntity createProduct(ProductEntity product, UserEntity user){
        product.setUser(user);
        return productRepository.save(product);
    }

    public List<ProductEntity> getProductsByUser(UserEntity user){
        return productRepository.findByUser(user);
    }
    
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}

package com.cintie.marketplace_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.services.ProductService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private ProductService productService;

    @GetMapping("/my")
    public List<ProductEntity> getMyProducts(@AuthenticationPrincipal UserEntity user) {
        return productService.getProductsByUser(user);
    }

    @PostMapping
    public ProductEntity createProduct(@RequestBody ProductEntity product, @AuthenticationPrincipal UserEntity user) {
        return productService.createProduct(product, user);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product");
        }
    }
}
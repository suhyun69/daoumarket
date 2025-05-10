// ProductController.java
package com.example.daoumarket.controller;

import com.example.daoumarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDetails(productId));
    }
}
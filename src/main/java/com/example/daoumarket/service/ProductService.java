package com.example.daoumarket.service;

import com.example.daoumarket.entity.Product;
import com.example.daoumarket.entity.ProductAddon;
import com.example.daoumarket.entity.ProductOptionGroup;
import com.example.daoumarket.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDetailResponse getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductOptionGroupDto> optionGroups = product.getOptionGroups().stream()
                .map(g -> new ProductOptionGroupDto(
                        g.getName(),
                        g.getOptions().stream()
                                .map(o -> new ProductOptionDto(o.getName(), o.getPriceDelta()))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        List<ProductAddonDto> addons = product.getAddons().stream()
                .map(a -> new ProductAddonDto(a.getName(), a.getPrice()))
                .collect(Collectors.toList());

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getBasePrice(),
                optionGroups,
                addons
        );
    }

    @Data
    @AllArgsConstructor
    public static class ProductDetailResponse {
        private Long id;
        private String name;
        private int basePrice;
        private List<ProductOptionGroupDto> optionGroups;
        private List<ProductAddonDto> addons;
    }

    @Data
    @AllArgsConstructor
    public static class ProductOptionGroupDto {
        private String groupName;
        private List<ProductOptionDto> options;
    }

    @Data
    @AllArgsConstructor
    public static class ProductOptionDto {
        private String name;
        private int priceDelta;
    }

    @Data
    @AllArgsConstructor
    public static class ProductAddonDto {
        private String name;
        private int price;
    }
}

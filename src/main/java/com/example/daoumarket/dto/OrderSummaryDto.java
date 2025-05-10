package com.example.daoumarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderSummaryDto {
    private ProductSummary product;
    private List<AddonSummary> addons;
    private int totalPrice;

    @Data @AllArgsConstructor
    public static class ProductSummary {
        private String name;
        private int quantity;
        private String option;
        private int unitPrice;
        private int total;
    }

    @Data @AllArgsConstructor
    public static class AddonSummary {
        private String name;
        private int price;
        private int quantity;
        private int total;
    }
}

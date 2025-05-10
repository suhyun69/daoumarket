package com.example.daoumarket.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long productId;
    private List<String> selectedOptions;
    private int productQuantity;
    private List<AddonRequest> addons;

    @Data
    public static class AddonRequest {
        private String name;
        private int quantity;
    }
}
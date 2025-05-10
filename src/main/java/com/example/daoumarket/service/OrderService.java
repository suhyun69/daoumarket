// OrderService.java
package com.example.daoumarket.service;

import com.example.daoumarket.dto.OrderRequest;
import com.example.daoumarket.dto.OrderResponse;
import com.example.daoumarket.dto.OrderSummaryDto;
import com.example.daoumarket.entity.*;
import com.example.daoumarket.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final ProductAddonRepository addonRepository;
    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest req) {

        // ✅ 수량 제한 (1~10)
        if (req.getProductQuantity() < 1 || req.getProductQuantity() > 10) {
            throw new IllegalArgumentException("수량은 1~10개 사이여야 합니다.");
        }

        // ✅ 옵션 선택 유무 확인
        if (req.getSelectedOptions() == null || req.getSelectedOptions().isEmpty()) {
            throw new IllegalArgumentException("옵션은 반드시 하나 이상 선택해야 합니다.");
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int basePrice = product.getBasePrice();
        int optionPrice = 0;
        StringBuilder optionSummary = new StringBuilder();

        for (String optionName : req.getSelectedOptions()) {
            ProductOption option = optionRepository.findByName(optionName)
                    .orElseThrow(() -> new RuntimeException("Invalid option: " + optionName));
            optionPrice += option.getPriceDelta();
            optionSummary.append(option.getName()).append("/");
        }

        // ✅ 상품 가격 = 기본 가격 + 옵션가
        int unitPrice = basePrice + optionPrice;
        int productTotal = unitPrice * req.getProductQuantity();

        List<OrderAddonItem> addonItems = new ArrayList<>();
        int addonTotal = 0;
        for (OrderRequest.AddonRequest addonReq : req.getAddons()) {
            ProductAddon addon = addonRepository.findByName(addonReq.getName())
                    .orElseThrow(() -> new RuntimeException("Invalid addon: " + addonReq.getName()));
            int total = addon.getPrice() * addonReq.getQuantity();
            addonItems.add(new OrderAddonItem(null, null, addonReq.getName(), addon.getPrice(), addonReq.getQuantity(), total));

            // ✅ 추가 상품 가격은 별도 합산
            addonTotal += total;
        }

        // ✅ 총 결제 금액 = 옵션 포함 상품 총합 + 추가상품 총합
        int totalPrice = productTotal + addonTotal;

        // ✅ 주문 저장 시 상품 정보 snapshot 저장
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PAID");
        order.setTotalPrice(totalPrice);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setOptionName(optionSummary.toString());
        item.setOptionPrice(optionPrice);
        item.setQuantity(req.getProductQuantity());
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(productTotal);
        item.setOrder(order);

        order.setItems(List.of(item));
        addonItems.forEach(ai -> ai.setOrder(order));
        order.setAddonItems(addonItems);

        orderRepository.save(order);

        return new OrderResponse(order.getId(), order.getTotalPrice(), order.getStatus());
    }

    public OrderSummaryDto summarizeOrder(OrderRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int basePrice = product.getBasePrice();
        int optionPrice = 0;
        StringBuilder optionSummary = new StringBuilder();

        for (String optionName : req.getSelectedOptions()) {
            ProductOption option = optionRepository.findByName(optionName)
                    .orElseThrow(() -> new RuntimeException("Invalid option: " + optionName));
            optionPrice += option.getPriceDelta();
            optionSummary.append(option.getName()).append("/");
        }

        int unitPrice = basePrice + optionPrice;
        int productTotal = unitPrice * req.getProductQuantity();

        List<OrderSummaryDto.AddonSummary> addonSummaries = new ArrayList<>();
        int addonTotal = 0;
        for (OrderRequest.AddonRequest addonReq : req.getAddons()) {
            ProductAddon addon = addonRepository.findByName(addonReq.getName())
                    .orElseThrow(() -> new RuntimeException("Invalid addon: " + addonReq.getName()));
            int total = addon.getPrice() * addonReq.getQuantity();
            addonSummaries.add(new OrderSummaryDto.AddonSummary(
                    addon.getName(), addon.getPrice(), addonReq.getQuantity(), total
            ));
            addonTotal += total;
        }

        return new OrderSummaryDto(
                new OrderSummaryDto.ProductSummary(
                        product.getName(),
                        req.getProductQuantity(),
                        optionSummary.toString(),
                        unitPrice,
                        productTotal
                ),
                addonSummaries,
                productTotal + addonTotal
        );
    }

    public OrderDetailDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderDetailDto.OrderItemDto> items = order.getItems().stream()
                .map(i -> new OrderDetailDto.OrderItemDto(
                        i.getProduct().getName(),
                        i.getOptionName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotalPrice()
                )).collect(Collectors.toList());

        List<OrderDetailDto.AddonItemDto> addons = order.getAddonItems().stream()
                .map(a -> new OrderDetailDto.AddonItemDto(
                        a.getAddonName(),
                        a.getQuantity(),
                        a.getAddonPrice()
                )).collect(Collectors.toList());

        return new OrderDetailDto(
                order.getId(),
                items,
                addons,
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }

    @Data
    @AllArgsConstructor
    public static class OrderDetailDto {
        private Long orderId;
        private List<OrderItemDto> items;
        private List<AddonItemDto> addons;
        private int totalPrice;
        private LocalDateTime createdAt;

        @Data
        @AllArgsConstructor
        public static class OrderItemDto {
            private String productName;
            private String optionName;
            private int quantity;
            private int unitPrice;
            private int totalPrice;
        }

        @Data
        @AllArgsConstructor
        public static class AddonItemDto {
            private String addonName;
            private int quantity;
            private int price;
        }
    }
}
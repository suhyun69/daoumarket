// OrderService.java
package com.example.daoumarket.service;

import com.example.daoumarket.dto.OrderRequest;
import com.example.daoumarket.dto.OrderResponse;
import com.example.daoumarket.dto.OrderSummaryDto;
import com.example.daoumarket.entity.*;
import com.example.daoumarket.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final ProductAddonRepository addonRepository;
    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest req) {
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

        List<OrderAddonItem> addonItems = new ArrayList<>();
        int addonTotal = 0;
        for (OrderRequest.AddonRequest addonReq : req.getAddons()) {
            ProductAddon addon = addonRepository.findByName(addonReq.getName())
                    .orElseThrow(() -> new RuntimeException("Invalid addon: " + addonReq.getName()));
            int total = addon.getPrice() * addonReq.getQuantity();
            addonItems.add(new OrderAddonItem(null, null, addonReq.getName(), addon.getPrice(), addonReq.getQuantity(), total));
            addonTotal += total;
        }

        int totalPrice = productTotal + addonTotal;

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

}
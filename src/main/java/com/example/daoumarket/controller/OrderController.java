// OrderController.java
package com.example.daoumarket.controller;

import com.example.daoumarket.dto.OrderRequest;
import com.example.daoumarket.dto.OrderResponse;
import com.example.daoumarket.dto.OrderSummaryDto;
import com.example.daoumarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PostMapping("/summary")
    public ResponseEntity<OrderSummaryDto> getOrderSummary(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.summarizeOrder(request));
    }

}
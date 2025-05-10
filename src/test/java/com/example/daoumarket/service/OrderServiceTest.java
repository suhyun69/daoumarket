package com.example.daoumarket.service;

import com.example.daoumarket.dto.OrderRequest;
import com.example.daoumarket.dto.OrderResponse;
import com.example.daoumarket.entity.*;
import com.example.daoumarket.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductOptionRepository optionRepository;
    @Mock private ProductAddonRepository addonRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderSuccess() {
        Product product = new Product();
        product.setId(1L);
        product.setName("아이폰");
        product.setBasePrice(1000000);

        ProductOption option = new ProductOption();
        option.setName("딥 퍼플");
        option.setPriceDelta(10000);

        ProductAddon addon = new ProductAddon();
        addon.setName("케이스");
        addon.setPrice(25000);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(optionRepository.findByName("딥 퍼플")).thenReturn(Optional.of(option));
        when(addonRepository.findByName("케이스")).thenReturn(Optional.of(addon));

        OrderRequest request = new OrderRequest();
        request.setProductId(1L);
        request.setSelectedOptions(List.of("딥 퍼플"));
        request.setProductQuantity(2);

        OrderRequest.AddonRequest addonRequest = new OrderRequest.AddonRequest();
        addonRequest.setName("케이스");
        addonRequest.setQuantity(1);
        request.setAddons(List.of(addonRequest));

        OrderResponse response = orderService.createOrder(request);

        assertEquals("PAID", response.getStatus());
        assertEquals((1000000 + 10000) * 2 + 25000, response.getTotalPrice());
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void testInvalidQuantityThrowsException() {
        OrderRequest request = new OrderRequest();
        request.setProductId(1L);
        request.setProductQuantity(0);
        request.setSelectedOptions(List.of("옵션"));
        request.setAddons(List.of());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
        assertEquals("수량은 1~10개 사이여야 합니다.", ex.getMessage());
    }

    @Test
    void testMissingOptionsThrowsException() {
        OrderRequest request = new OrderRequest();
        request.setProductId(1L);
        request.setProductQuantity(1);
        request.setSelectedOptions(List.of());
        request.setAddons(List.of());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
        assertEquals("옵션은 반드시 하나 이상 선택해야 합니다.", ex.getMessage());
    }
}

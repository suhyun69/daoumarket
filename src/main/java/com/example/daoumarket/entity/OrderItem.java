// OrderItem.java
package com.example.daoumarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private String optionName;
    private int optionPrice;
    private int quantity;
    private int unitPrice;
    private int totalPrice;
}
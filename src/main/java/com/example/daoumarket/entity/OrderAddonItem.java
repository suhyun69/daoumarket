// OrderAddonItem.java
package com.example.daoumarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddonItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    private String addonName;
    private int addonPrice;
    private int quantity;
    private int totalPrice;
}

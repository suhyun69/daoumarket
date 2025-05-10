// Product.java
package com.example.daoumarket.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int basePrice;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductOptionGroup> optionGroups;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductAddon> addons;
}
// ProductOption.java
package com.example.daoumarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int priceDelta;

    @ManyToOne
    private ProductOptionGroup optionGroup;
}
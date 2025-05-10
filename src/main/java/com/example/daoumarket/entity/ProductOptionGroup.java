// ProductOptionGroup.java
package com.example.daoumarket.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Product product;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL)
    private List<ProductOption> options;
}

// ProductAddonRepository.java
package com.example.daoumarket.repository;

import com.example.daoumarket.entity.ProductAddon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAddonRepository extends JpaRepository<ProductAddon, Long> {
    Optional<ProductAddon> findByName(String name);
}
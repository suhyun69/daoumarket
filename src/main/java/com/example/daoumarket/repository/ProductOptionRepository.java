// ProductOptionRepository.java
package com.example.daoumarket.repository;

import com.example.daoumarket.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    Optional<ProductOption> findByName(String name);
}
// OrderRepository.java
package com.example.daoumarket.repository;

import com.example.daoumarket.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
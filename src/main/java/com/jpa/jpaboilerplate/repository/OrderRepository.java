package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

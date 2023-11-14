package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}

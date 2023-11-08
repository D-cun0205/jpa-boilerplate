package com.jpa.jpaboilerplate.repository.order.simplequery;

import com.jpa.jpaboilerplate.domain.Address;
import com.jpa.jpaboilerplate.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long id, String name, LocalDateTime datetime, OrderStatus status, Address address) {
        this.orderId = id;
        this.name = name;
        this.orderDate = datetime;
        this.orderStatus = status;
        this.address = address;
    }
}

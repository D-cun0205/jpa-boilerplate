package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;
}

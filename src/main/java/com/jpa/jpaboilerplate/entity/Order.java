package com.jpa.jpaboilerplate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order 는 예약어로 사용되는 경우가 많아서 변경
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList = new ArrayList<>();

    @ElementCollection
    private List<String> strings = new ArrayList<>();

    @Enumerated
    private OrderStatus orderStatus;

    public void addChild(OrderItem child) {
        orderItemList.add(child);
        child.setOrder(this);
    }
}

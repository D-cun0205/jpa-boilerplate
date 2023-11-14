package com.jpa.jpaboilerplate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@ToString(of = {"itemName"})
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    private String itemName;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(String itemName) {
        this.itemName = itemName;
    }
}

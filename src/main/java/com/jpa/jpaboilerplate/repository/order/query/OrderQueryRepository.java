package com.jpa.jpaboilerplate.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        var order = findOrder();
        order.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return order;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> order = findOrder();
        List<Long> orderIds = order.stream().map(o -> o.getOrderId()).collect(toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new com.jpa.jpaboilerplate.repository.order.query.OrderItemQueryDto(oi.id, i.name, oi.orderPrice, oi.count) from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> map = orderItems.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderId()));

        order.forEach(o -> o.setOrderItems(map.get(o.getOrderId())));
        return order;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new com.jpa.jpaboilerplate.repository.order.query.OrderItemQueryDto(oi.id, i.name, oi.orderPrice, oi.count) from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrder() {
        return em.createQuery(
                "select new com.jpa.jpaboilerplate.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }
}

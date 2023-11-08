package com.jpa.jpaboilerplate.api;

import com.jpa.jpaboilerplate.domain.Address;
import com.jpa.jpaboilerplate.domain.Order;
import com.jpa.jpaboilerplate.domain.OrderItem;
import com.jpa.jpaboilerplate.domain.OrderStatus;
import com.jpa.jpaboilerplate.repository.OrderRepository;
import com.jpa.jpaboilerplate.repository.OrderSearch;
import com.jpa.jpaboilerplate.repository.order.query.OrderQueryDto;
import com.jpa.jpaboilerplate.repository.order.query.OrderQueryRepository;
import com.jpa.jpaboilerplate.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderService orderService;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(orderItem -> orderItem.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v5/orders")
    public List<OrderDto> ordersV5() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
//        return orderQueryRepository.findOrderQueryDtos();
        return orderQueryRepository.findAllByDto_optimization();
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;
        private int price;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.price = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}

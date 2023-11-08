package com.jpa.jpaboilerplate.api;

import com.jpa.jpaboilerplate.domain.Address;
import com.jpa.jpaboilerplate.domain.Order;
import com.jpa.jpaboilerplate.domain.OrderStatus;
import com.jpa.jpaboilerplate.repository.OrderRepository;
import com.jpa.jpaboilerplate.repository.order.simplequery.OrderSimpleQueryDto;
import com.jpa.jpaboilerplate.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v3/orders")
    public List<SelectAllOrderResponse> ordersV3() {
        return orderRepository.findAllWithFetch().stream()
                .map(SelectAllOrderResponse::new)
                .collect(toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SelectAllOrderResponse {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SelectAllOrderResponse(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}


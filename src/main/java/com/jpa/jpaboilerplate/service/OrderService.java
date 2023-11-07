package com.jpa.jpaboilerplate.service;

import com.jpa.jpaboilerplate.domain.Delivery;
import com.jpa.jpaboilerplate.domain.Member;
import com.jpa.jpaboilerplate.domain.Order;
import com.jpa.jpaboilerplate.domain.OrderItem;
import com.jpa.jpaboilerplate.domain.item.Item;
import com.jpa.jpaboilerplate.repository.ItemRepository;
import com.jpa.jpaboilerplate.repository.MemberRepository;
import com.jpa.jpaboilerplate.repository.OrderRepository;
import com.jpa.jpaboilerplate.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        orderRepository.save(order);
        return order.getId();
    }

    // 취소
    @Transactional
    public void cancel(Long orderId) {
        // Order 해당 테이블에 있는 order 를 삭제해야됨
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}

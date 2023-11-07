package com.jpa.jpaboilerplate.service;

import com.jpa.jpaboilerplate.domain.Address;
import com.jpa.jpaboilerplate.domain.Member;
import com.jpa.jpaboilerplate.domain.Order;
import com.jpa.jpaboilerplate.domain.OrderStatus;
import com.jpa.jpaboilerplate.domain.item.Book;
import com.jpa.jpaboilerplate.domain.item.Item;
import com.jpa.jpaboilerplate.exception.NotEnoughStockException;
import com.jpa.jpaboilerplate.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void 상품_주문() throws Exception {
        //given
        Member member = createMember("회원1", new Address("Seoul", "river", "123-123"));
        Item book = createBook("countryside JPA", 10000, 10);
        int orderCount = 2;

        //when
        long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, findOrder.getStatus(), "상품 주문시 상태는 ORDER.");
        assertEquals(1, findOrder.getOrderItems().size(), "주문한 상품 종류 수가정확해야 한다.");
        assertEquals(10000 * orderCount, findOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @Test
    void 주문_취소() throws Exception {
        //given
        Member member = createMember("dcun", new Address("Seoul", "river", "123-123"));
        Item item = createBook("book", 10000, 10);
        int orderCount = 2;
        long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancel(orderId);

        //then
        Order findOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, findOrder.getStatus(), "주문 취소시 상태는 CANCEL 이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 된다.");
    }

    @Test
    void 상품_주문_재고_수량_초과() throws Exception {
        //given
        Member member = createMember("회원1", new Address("Seoul", "river", "123-123"));
        Item item = createBook("countryside JPA", 10000, 10);
        int orderCount = 11;

        //when, then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }
}
package com.jpa.jpaboilerplate;

import com.jpa.jpaboilerplate.entity.Order;
import com.jpa.jpaboilerplate.entity.OrderItem;
import com.jpa.jpaboilerplate.repository.OrderItemRepository;
import com.jpa.jpaboilerplate.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class JpaboilerplateApplicationTests {
	@Autowired EntityManager em;
	@Autowired OrderRepository orderRepository;
	@Autowired OrderItemRepository orderItemRepository;

//	JPAQueryFactory queryFactory;
//
//	@BeforeEach
//	void init() {
//		queryFactory = new JPAQueryFactory(em);
//		Team teamA = new Team("teamA");
//		em.persist(teamA);
//		Team teamB = new Team("teamB");
//		em.persist(teamB);
//
//		Member memberA = new Member("usernameA", 10, teamA);
//		em.persist(memberA);
//		Member memberB = new Member("usernameB", 20, teamA);
//		em.persist(memberB);
//		Member memberC = new Member("usernameC", 30, teamB);
//		em.persist(memberC);
//		Member memberD = new Member("usernameD", 40, teamB);
//		em.persist(memberD);
//	}

	@Test
	void cascade() throws Exception {
		OrderItem iphone = new OrderItem("iphone15");
		OrderItem galaxy = new OrderItem("galaxyS23");
		Order order = new Order();
		order.addChild(iphone);
		order.addChild(galaxy);
		em.persist(order);

		em.flush();
		em.clear();

		Order findOrder = orderRepository.findById(order.getId()).get();
		em.remove(findOrder);

		em.flush();
		em.clear();

		List<OrderItem> orderItems = orderItemRepository.findAll();
		for (OrderItem orderItem : orderItems) {
			System.out.println("orderItem = " + orderItem);
		}
	}
}

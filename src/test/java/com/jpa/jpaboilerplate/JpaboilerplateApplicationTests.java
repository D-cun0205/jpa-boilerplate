package com.jpa.jpaboilerplate;

import com.jpa.jpaboilerplate.entity.Member;
import com.jpa.jpaboilerplate.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JpaboilerplateApplicationTests {

	@Autowired
	EntityManager em;
	JPAQueryFactory queryFactory;

	@BeforeEach
	void init() {
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		em.persist(teamA);
		Team teamB = new Team("teamB");
		em.persist(teamB);

		Member memberA = new Member("usernameA", 10, teamA);
		em.persist(memberA);
		Member memberB = new Member("usernameB", 20, teamA);
		em.persist(memberB);
		Member memberC = new Member("usernameC", 30, teamB);
		em.persist(memberC);
		Member memberD = new Member("usernameD", 40, teamB);
		em.persist(memberD);
	}
}

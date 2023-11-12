package com.jpa.jpaboilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JpaboilerplateApplication {
	public static void main(String[] args) {
		SpringApplication.run(JpaboilerplateApplication.class, args);
	}

	// 빈으로 주입 받고 레파지토리에서 생성자 매개변수에 넣고 바로 주입해주어도 된다
	// 선호하는 스타일로 적용
//	@Bean
//	JPAQueryFactory jpaQueryFactory(EntityManager em) {
//		return new JPAQueryFactory(em);
//	}
}

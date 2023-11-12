package com.jpa.jpaboilerplate.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;
    public MemberQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 쿼리가 특정 기능을 하거나 복잡하여 길어지는 경우 위 와 같이 별도로 생성하고 Repository 로 등록 후 사용
}

package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.dto.MemberTeamDto;
import com.jpa.jpaboilerplate.dto.QMemberTeamDto;
import com.jpa.jpaboilerplate.entity.Member;
import com.jpa.jpaboilerplate.entity.MemberSearchCondition;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.jpa.jpaboilerplate.entity.QMember.member;
import static com.jpa.jpaboilerplate.entity.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Member> findMemberCustom() {
        return queryFactory
                .selectFrom(member)
                .fetch();
    }

    @Override
    public Page<MemberTeamDto> findMemberCustom(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // countQuery 에서 fetchOne() 메서드를 호출하지 않고 함수를 넘겨주는 이유는
        // 아래와 같이 getPage 에 함수를 넘겨주면 해당 객체의 메커니즘에 의해 자동으로 성능 최적화를 한다
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private Predicate usernameEq(String username) {
        return username != null ? member.username.eq(username) : null;
    }

    private Predicate teamNameEq(String teamName) {
        return teamName != null ? team.name.eq(teamName) : null;
    }

    private Predicate ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private Predicate ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

}

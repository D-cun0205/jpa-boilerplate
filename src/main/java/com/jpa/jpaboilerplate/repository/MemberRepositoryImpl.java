package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.dto.MemberTeamDto;
import com.jpa.jpaboilerplate.entity.MemberSearchCondition;
import com.jpa.jpaboilerplate.repository.support.QuerydslRepositorySupportCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.jpa.jpaboilerplate.entity.QMember.member;
import static com.jpa.jpaboilerplate.entity.QTeam.team;

public class MemberRepositoryImpl extends QuerydslRepositorySupportCustom implements MemberRepositoryCustom {
    public MemberRepositoryImpl(Class<?> domainClass) {
        super(domainClass);
    }

    public Page<MemberTeamDto> searchMemberCustom(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable, query -> query
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                ));
    }

    public Page<MemberTeamDto> searchMemberCustomAndCount(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(
                pageable,
                // content(Data)
                query -> query
                        .selectFrom(member)
                        .leftJoin(member.team, team)
                        .where(
                                usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())
                        ),
                // count
                countQuery -> countQuery
                        .select(member.id)
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(
                                usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())
                        )
        );
    }

    private BooleanExpression usernameEq(String username) {
        return username != null ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return teamName != null ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }
}

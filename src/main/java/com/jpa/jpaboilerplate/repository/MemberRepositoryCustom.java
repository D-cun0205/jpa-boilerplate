package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.dto.MemberTeamDto;
import com.jpa.jpaboilerplate.entity.Member;
import com.jpa.jpaboilerplate.entity.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
    Page<MemberTeamDto> findMemberCustom(MemberSearchCondition condition, Pageable pageable);
}

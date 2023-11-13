package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.dto.MemberTeamDto;
import com.jpa.jpaboilerplate.entity.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberTeamDto> searchMemberCustom(MemberSearchCondition condition, Pageable pageable);
}

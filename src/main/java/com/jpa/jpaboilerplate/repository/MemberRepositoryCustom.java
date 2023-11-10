package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}

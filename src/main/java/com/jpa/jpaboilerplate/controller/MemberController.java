package com.jpa.jpaboilerplate.controller;

import com.jpa.jpaboilerplate.dto.MemberTeamDto;
import com.jpa.jpaboilerplate.entity.MemberSearchCondition;
import com.jpa.jpaboilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.findMemberCustom(condition, pageable);
    }
}

package com.jpa.jpaboilerplate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long memberId;
    private String name;
    private int age;
    private String teamName;
}

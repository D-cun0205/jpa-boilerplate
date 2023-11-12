package com.jpa.jpaboilerplate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSearchCondition {
    private String username;
    private String teamName;
    private int ageGoe;
    private int ageLoe;
}

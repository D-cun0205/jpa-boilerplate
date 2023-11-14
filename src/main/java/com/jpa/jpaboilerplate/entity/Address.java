package com.jpa.jpaboilerplate.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString(of = {"city", "street", "zipCode"})
public class Address {
    private String city;
    private String street;
    private String zipCode;
}

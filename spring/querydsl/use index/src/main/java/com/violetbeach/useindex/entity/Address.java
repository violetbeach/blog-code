package com.violetbeach.useindex.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Address {

    @Column(name = "address")
    private String value;

}

package com.violetbeach.useindex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", indexes = @Index(name = "ix_username", columnList = "username"))
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	private String username;
	private Address address;

	public Member(String username) {
		this.username = username;
	}

}
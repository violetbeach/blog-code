package com.violetbeach.useindex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Immutable
public class MemberFiltering {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	private String username;

	public MemberFiltering(String username) {
		this.username = username;
	}

}
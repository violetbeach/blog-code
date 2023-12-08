package com.violetbeach.useindex.repository;


import static com.violetbeach.useindex.entity.QMember.*;
import static com.violetbeach.useindex.entity.QMemberFiltering.memberFiltering;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import com.violetbeach.useindex.entity.QMember;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;
	private final MySQLQueryFactory mySQLQueryFactory;
	private final QMember member = new QMember("member");

	public MemberRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager,
		MySQLQueryFactory mySQLQueryFactory) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
		this.mySQLQueryFactory = mySQLQueryFactory;
	}

	@Override
	public List<Long> search(String name) {
		return mySQLQueryFactory
			.select(member.id)
			.from(member)
			.useIndex("ix_username")
			.where(member.username.like(name))
			.fetch();
	}
}

package com.violetbeach.useindex.repository;


import static com.violetbeach.useindex.entity.QMember.*;
import static com.violetbeach.useindex.entity.QMemberFiltering.memberFiltering;
import static com.violetbeach.useindex.inspector.IndexHintInspector.getInspectorIndexHint;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import com.violetbeach.useindex.entity.QMember;
import java.util.List;
import javax.persistence.EntityManager;

import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	public MemberRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
	}

	@Override
	public List<Long> search(String name) {
		return queryFactory
			.select(member.id)
			.from(member)
			.where(member.username.like(name))
			.setHint(QueryHints.COMMENT, getInspectorIndexHint("USE INDEX (ix_username)"))
			.fetch();
	}
}

package com.violetbeach.aop.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.violetbeach.aop.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.violetbeach.aop.entity.QMember.member;

@Repository
@Transactional(readOnly = true)
public class MemberRepositoryImpl implements MemberRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public MemberRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<Member> search(String name) {
		return queryFactory
				.select(member)
				.from(member)
				.where(member.username.like(name))
				.fetch();
	}
}

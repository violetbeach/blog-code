package com.violetbeach.aop.repository;

import org.springframework.beans.factory.annotation.Autowired;

class ProxyTargetClassFalse extends RepositoryTestSupport{

	@Autowired
	MemberRepositoryImpl memberRepositoryImpl;

	@org.junit.jupiter.api.Test
	void search() {
		memberRepositoryImpl.search("Hi");
	}

}
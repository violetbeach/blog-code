package com.violetbeach.useindex.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryImplTest extends RepositoryTestSupport {
	@Autowired
	MemberRepository memberRepository;

	@Test
	void search() {
		memberRepository.search("hi");
	}

}
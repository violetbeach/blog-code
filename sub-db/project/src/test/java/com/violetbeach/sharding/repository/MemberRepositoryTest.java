package com.violetbeach.sharding.repository;

import com.violetbeach.sharding.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryTest extends RepositoryTestSupport {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void save() {
        Member member = new Member("username");
        memberRepository.save(member);
    }

}
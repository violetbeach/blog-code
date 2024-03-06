package com.violetbeach.aop.repository;

import com.violetbeach.aop.entity.Member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryImplTest extends RepositoryTestSupport {

    @Autowired
    MemberRepository memberRepositoryImpl;

    @Test
    void search() {
        Member hi = new Member("Hi");
        memberRepositoryImpl.search("hi");
    }

}
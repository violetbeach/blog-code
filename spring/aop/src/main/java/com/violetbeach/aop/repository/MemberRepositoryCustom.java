package com.violetbeach.aop.repository;

import com.violetbeach.aop.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> search(String name);
}

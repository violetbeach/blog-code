package com.violetbeach.useindex.repository;

import com.violetbeach.useindex.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

}

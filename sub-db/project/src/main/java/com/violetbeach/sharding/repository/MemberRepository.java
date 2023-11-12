package com.violetbeach.sharding.repository;

import com.violetbeach.sharding.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

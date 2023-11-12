package com.violetbeach.sharding;

import com.violetbeach.sharding.entity.Member;
import com.violetbeach.sharding.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final MemberRepository memberRepository;

    @PostMapping("/members")
    public ResponseEntity<Member> postMember(String username) {
        Member member = new Member(username);
        memberRepository.save(member);
        return ResponseEntity.ok(member);
    }

}

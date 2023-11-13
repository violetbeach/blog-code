package com.violetbeach.sharding;

import com.violetbeach.sharding.entity.Member;
import com.violetbeach.sharding.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final MemberRepository memberRepository;

    @PostMapping("/members")
    public ResponseEntity<Member> postMember(@RequestBody PostRequest postRequest) {
        Member member = new Member(postRequest.getUsername());
        memberRepository.save(member);
        return ResponseEntity.ok(member);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostRequest {
        private String username;
    }

}

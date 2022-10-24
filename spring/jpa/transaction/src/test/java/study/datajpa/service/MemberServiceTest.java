package study.datajpa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import study.datajpa.entity.CreateMemberRequest;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    void create() {
        CreateMemberRequest request = new CreateMemberRequest("violetBeach1", new MockMultipartFile("file", "String".getBytes()));

        Member member = memberService.create(request);

        assertThat(member.getUsername()).isEqualTo(request.getUsername());
    }

}
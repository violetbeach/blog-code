package study.datajpa.service;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import study.datajpa.entity.CreateMemberRequest;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberResourceService resourceService;
    private final LazyConnectionDataSourceProxy dataSource;


    @Transactional(readOnly = true)
    public Member create(CreateMemberRequest request) {
        printConnectionStatus();

        Member member = new Member(request.getUsername());
        memberRepository.save(member);

        resourceService.upload(request.getUserImage());

        printConnectionStatus();
        return member;
    }

    private void printConnectionStatus() {
        final HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource)dataSource.getTargetDataSource()).getHikariPoolMXBean();
        System.out.println("################################");
        System.out.println("현재 active인 connection의 수 : " + hikariPoolMXBean.getActiveConnections());
        System.out.println("현재 idle인 connection의 수 : " + hikariPoolMXBean.getIdleConnections());
        System.out.println("################################");
    }

}

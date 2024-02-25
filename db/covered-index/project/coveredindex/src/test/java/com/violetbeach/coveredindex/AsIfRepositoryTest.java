package com.violetbeach.coveredindex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class AsIfRepositoryTest {

    @Autowired AsIfRepository asIfRepository;

    @Test
    void test() {
        asIfRepository.findList("KR", PageRequest.of(0, 50));
    }

}
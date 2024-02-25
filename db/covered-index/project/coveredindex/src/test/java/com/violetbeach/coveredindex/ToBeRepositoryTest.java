package com.violetbeach.coveredindex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class ToBeRepositoryTest {

    @Autowired
    ToBeRepository toBeRepository;

    @Test
    void test() {
        toBeRepository.findList("KR", PageRequest.of(0, 50));
    }

}
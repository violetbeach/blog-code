package com.violetbeach.coveredindex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class ArticleRepositoryImplTest {

    @Autowired
    ArticleRepositoryImpl articleRepository;

    @Test
    void test() {
        articleRepository.findList("KR", PageRequest.of(0, 50));
    }

}
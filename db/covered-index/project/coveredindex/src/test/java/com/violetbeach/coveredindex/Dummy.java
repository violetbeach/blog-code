package com.violetbeach.coveredindex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Dummy {

    @Autowired CategoryRepository categoryRepository;
    @Autowired ArticleRepository articleRepository;
    @Autowired ArticleAuthRepository articleAuthRepository;

    long categoryIndex = 1;

    @Test
    void test() {
        while(categoryIndex < 100000) {
            Category category = categoryRepository.save(new Category(categoryIndex, true, "name_" + categoryIndex++));

            // if(categoryIndex < 50) {
            //     int articleIndex = 0;
            //     while(articleIndex < 100000) {
            //         Article article = new Article("subject_" + articleIndex++, "content", category.categoryId);
            //         articleRepository.save(article);
            //
            //         if(article.getArticleId() %2 == 0) {
            //             articleAuthRepository.save(new ArticleAuth("content", article.getArticleId()));
            //         }
            //     }
            // }
        }
    }
}

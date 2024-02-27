package com.violetbeach.coveredindex;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class Dummy {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    ArticleAuthRepository articleAuthRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    long categoryIndex = 1;

    // @Test
    // void test() {
    //     while (categoryIndex < 100000) {
    //         boolean isPublic = categoryIndex % 5 != 0;
    //         Category category = categoryRepository.save(
    //             new Category(categoryIndex, isPublic, "name_" + categoryIndex++));
    //
    //         if (categoryIndex < 50) {
    //             int articleIndex = 0;
    //             while (articleIndex < 100000) {
    //                 Article article = new Article("subject_" + articleIndex++, "content", category.categoryId);
    //                 articleRepository.save(article);
    //
    //                 if (article.getArticleId() % 2 == 0) {
    //                     articleAuthRepository.save(new ArticleAuth("content", article.getArticleId()));
    //                 }
    //             }
    //         }
    //     }
    // }

    @Test
    void categorySave() {
        List<Category> categories = new ArrayList<>();
        while (categoryIndex < 100000) {
            boolean isPublic = categoryIndex % 5 != 0;
            categories.add(
                new Category(categoryIndex, isPublic, "name_" + categoryIndex++)
            );
        }

        String sql = "INSERT INTO category (is_public, name) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql,
            categories,
            1000,
            (PreparedStatement ps, Category category) -> {
                ps.setBoolean(1, category.isPublic());
                ps.setString(2, category.getName());
            });
    }

    @Test
    void articleSave() {
        while (categoryIndex < 80) {
            List<Article> articles = new ArrayList<>();
            int articleIndex = 0;
            while (articleIndex < 70000) {
                articles.add(new Article("subject_" + articleIndex++, "content", categoryIndex));
            }
            String sql = "INSERT INTO article (subject, content, category_id) VALUES (?, ?, ?)";
            jdbcTemplate.batchUpdate(sql,
                articles,
                1000,
                (PreparedStatement ps, Article article) -> {
                    ps.setString(1, article.getSubject());
                    ps.setString(2, article.getContent());
                    ps.setLong(3, article.getCategoryId());
                });

            categoryIndex++;
        }
    }

    @Test
    void articleAuthSave() {
        int articleIndex = 0;
        for (int i = 1; i < 10; i++) {
            List<ArticleAuth> articleAuth = new ArrayList<>();
            while (articleIndex / i < 2000000) {
                articleAuth.add(new ArticleAuth("content", (long)articleIndex++));
            }
            String sql = "INSERT INTO article_auth (content, article_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql,
                articleAuth,
                1000,
                (PreparedStatement ps, ArticleAuth auth) -> {
                    ps.setString(1, auth.getContent());
                    ps.setLong(2, auth.getArticleId());
                });
        }
    }
}

package com.violetbeach.coveredindex;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ArticleInfo {
    private final Long articleId;
    private final String subject;
    private final String content;
    private final String categoryId;
    private final Long articleAuthId;

    @QueryProjection
    public ArticleInfo(Article article, ArticleAuth articleAuth) {
        this.articleId = article.getArticleId();
        this.subject = article.getSubject();
        this.content = article.getContent();
        this.categoryId = article.getCategoryId();
        this.articleAuthId = articleAuth.getArticleAuthId();
    }

    @QueryProjection
    public ArticleInfo(Long articleId, String subject, String content, String categoryId, Long authId) {
        this.articleId = articleId;
        this.subject = subject;
        this.content = content;
        this.categoryId = categoryId;
        this.articleAuthId = authId;
    }
}

package com.violetbeach.coveredindex;

import static com.violetbeach.coveredindex.QArticle.*;
import static com.violetbeach.coveredindex.QArticleAuth.*;
import static com.violetbeach.coveredindex.QCategory.*;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class AsIsRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final Querydsl querydsl;

    public AsIsRepository(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.querydsl = new Querydsl(entityManager, new PathBuilderFactory().create(Article.class));
    }

    public Page<ArticleInfo> findList(String regionCode, Pageable pageable) {
        JPAQuery<ArticleInfo> baseQuery = jpaQueryFactory
            .select(new QArticleInfo(
                article.articleId,
                article.subject,
                article.content,
                category.categoryId,
                articleAuth.articleAuthId
            ))
            .from(article)
            .leftJoin(category).on(category.categoryId.eq(article.categoryId))
            .leftJoin(articleAuth)
            .on(articleAuth.articleId.eq(article.articleId))
            .where(
                article.regionCode.eq(regionCode),
                category.isPublic.eq(true).or(category.categoryId.isNull())
            );

        JPQLQuery<ArticleInfo> pagingQuery = querydsl.applyPagination(pageable, baseQuery);

        JPAQuery<Long> countQuery = createCountQuery(baseQuery.getMetadata().getWhere());

        return PageableExecutionUtils.getPage(pagingQuery.fetch(), pageable, countQuery::fetchOne);
    }

    private JPAQuery<Long> createCountQuery(Predicate whereCondition) {
        return jpaQueryFactory.select(article.count())
            .from(article)
            .where(whereCondition)
            .leftJoin(category)
            .on(category.categoryId.eq(article.categoryId));
    }
}

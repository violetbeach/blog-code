package com.violetbeach.coveredindex;

import static com.violetbeach.coveredindex.QArticle.*;
import static com.violetbeach.coveredindex.QArticleAuth.*;
import static com.violetbeach.coveredindex.QCategory.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ArticleRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final CategoryRepository categoryRepository;
    private final Querydsl querydsl;

    public ArticleRepositoryImpl(JPAQueryFactory jpaQueryFactory, CategoryRepository categoryRepository,
        EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.categoryRepository = categoryRepository;
        this.querydsl = new Querydsl(entityManager, new PathBuilderFactory().create(Article.class));
    }

    public Page<ArticleInfo> findList(String regionCode, Pageable pageable) {
        JPAQuery<Long> idsQuery = jpaQueryFactory
            .select(article.articleId)
            .from(article)
            .where(
                article.regionCode.eq(regionCode),
                category.isPublic.eq(true).or(category.categoryId.isNull())
            );

        List<Long> ids = querydsl.applyPagination(pageable, idsQuery).fetch();

        JPAQuery<Long> countQuery = createCountQuery(idsQuery.getMetadata().getWhere());

        JPAQuery<ArticleInfo> dataQuery = jpaQueryFactory.select(new QArticleInfo(article, articleAuth))
            .from(article)
            .leftJoin(articleAuth)
            .on(articleAuth.articleId.eq(article.articleId))
            .where(
                article.articleId.in(ids)
            );

        List<ArticleInfo> result = querydsl.applySorting(pageable.getSort(), dataQuery).fetch();

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    private JPAQuery<Long> createCountQuery(Predicate whereCondition) {
        return jpaQueryFactory.select(article.count())
            .from(article)
            .where(whereCondition)
            .leftJoin(category)
            .on(category.categoryId.eq(article.categoryId));
    }
}

package com.violetbeach.coveredindex;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @Table(indexes = @Index(name = "ix_region_code_category_id", columnList = "regision_code,category_id"))
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;
    private String subject;
    private String content;
    private String regionCode;
    private String categoryId;
    @Lob
    private String additional1;
    @Lob
    private String additional2;
    @Lob
    private String additional3;
    @Lob
    private String additional4;
    @Lob
    private String additional5;
    @Lob
    private String additional6;
    @Lob
    private String additional7;
    @Lob
    private String additional8;
    @Lob
    private String additional9;

    public Article(String subject, String content, String categoryId) {
        this.subject = subject;
        this.content = content;
        this.categoryId = categoryId;
    }
}

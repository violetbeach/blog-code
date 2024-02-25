package com.violetbeach.coveredindex;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @Table(indexes = @Index(name = "ix_article_id", columnList = "article_id"))
public class ArticleAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long articleAuthId;
    private String content;
    private Long articleId;
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

    public ArticleAuth(String content, Long articleId) {
        this.content = content;
        this.articleId = articleId;
    }
}

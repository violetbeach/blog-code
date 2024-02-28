package com.violetbeach.coveredindex;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    String categoryId;
    private boolean isPublic;
    private String name;
    private String regionCode;
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

    public Category(String categoryId, boolean isPublic, String name) {
        this.categoryId = categoryId;
        this.isPublic = isPublic;
        this.name = name;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}

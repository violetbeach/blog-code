## Mapping Construct

JPA, Hibernate에는 세 가지 매핑 구조가 있다.
- Basic types (Integer, Long, String, CustomStatus)
- Embeddable types (@Embeddable 애노테이션처럼 여러 컬럼을 묶은 것)
- Entity types (테이블과 맵핑)

맵핑할 타입이 더 컴팩트할 수록 더 높은 성능을 낼 수 있다.

가령, Status라는 Enum이 있다면 String보다 Ordinal을 사용한다면 더 적은 컬럼 타입을 사용하여 이점을 누릴 수 있고, 반면 DB를 봤을 때 직관적이지 않다는 단점이 있다.

그래서 보통 String을 선택하게 된다.

하지만, 다른 방법도 있다. CustomStatus 테이블을 하나 만들면 된다.

```java
@Entity(name = "Post")
@Table(name = "post")
public class Post {

    @Id
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint")
    private PostStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", insertable = false, updatable = false)
    private PostStatusInfo statusInfo;

    private String title;
}

@Entity(name = "PostStatusInfo")
@Table(name = "post_status_info")
public class PostStatusInfo {
    @Id
    @Column(columnDefinition = "tinyint")
    private Integer id;
    private String name;
    private String description;
}
```

사용하는 테이블에서는 Ordinal로 사용하고, `PostStatusInfo`를 맵핑할 테이블에 설명을 저장한다면 테이블을 맵핑할 때 드는 비용을 줄일 수 있다.

description 정보가 필요할 때는 `PostStatusInfo`를 조회하면 된다.

성능이 매우 중요한 환경에서는 이러한 기술을 사용한다면 이점이 있을 것이다.



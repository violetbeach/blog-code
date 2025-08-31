지난 번에 게시글로도 작성했었지만, DB에서 `Status`와 같은 성격을 띄는 컬럼에 대해서 다뤄본적이 있었다.
- VARCHAR로 관리할 지 vs Enum vs Tinyint, ...

최근에 Hibernate를 활용한 새로운 접근(?)을 알게 되었고 꽤 인상깊어서 공유하고자 한다. 

## Hibernate - Mapping Construct

JPA, Hibernate에는 세 가지 매핑 구조가 있다.
- Basic types (Integer, Long, String, CustomStatus)
- Embeddable types (@Embeddable 애노테이션처럼 여러 컬럼을 묶은 것)
- Entity types (테이블과 맵핑)

맵핑할 타입이 더 컴팩트할 수록 더 높은 성능을 낼 수 있다.

가령, Status라는 Enum이 있다면 String보다 Ordinal을 사용한다면 더 적은 컬럼 타입을 사용하여 이점을 누릴 수 있고, 반면 DB를 봤을 때 직관적이지 않다는 단점이 있다.

그래서 보통 String을 선택하게 된다. 당연히 테이블, 인덱스 크기가 커지고 각 쿼리의 크기도 커진다.

소개할 방법은 `Status`와 같은 테이블을 하나 만들면 된다. 아래 테이블과 맵핑한 엔터티를 보자.

```java
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

해당 엔터티는 id로 Enum의 Ordinal Value를 저장하게 된다. 아래는 해당 상태를 실제로 사용하는 엔터티이다.

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
```

사용하는 테이블에서는 Ordinal로 맵핑을 시킨다. `PostStatusInfo`를 맵핑할 테이블에 설명을 저장한다면 테이블을 맵핑할 때 드는 비용을 줄일 수 있다.

description 정보가 필요할 때는 `PostStatusInfo`를 조회하면 된다.

성능이 매우 중요한 환경에서는 이러한 기술을 사용한다면 이점이 있을 것이다.

## IPv4

응용을 한번 더 해보자. 만약 192.168.123.231/24 처럼 IPv4를 저장한돠면 아래의 방법을 떠올릴 것이다.
- bigint
- VARCHAR(18)
- 비트마스크 사용

PostgreSQL을 사용한다면 inet이라는 타입을 지원하고, Hibernate에서도 해당 타입을 사용할 수 있도록 할 수 있다.
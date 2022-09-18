# JUnit - Fixture 사용하기!

Fixture는 '고정되어 있는 물체'를 의미한다.

JUnit 테스트의 Fixture는 테스트 실행을 위한 베이스라인으로 사용되는 객체들의 고정된 상태이다.
Fixture의 목적은 결과를 반복가능할 수 있도록 알 수 있고, 고정된 환경에서 테스트할 수 있음을 보장한다.

예시를 들어보자.

- 게시글을 생성할 떄는 유저 정보가 필요하다.
- 댓글을 생성할 때도 유저 정보가 필요하다.
- 결제를 할 때도 유저 정보가 필요하다.
- 유저 정보 조회를 위해서도 유저 정보가 필요하다.

가령, 서비스 단위 테스트를 한다고 생각해보자.

대부분의 테스트에서는 유저를 생성하고, 해당 유저 정보를 사용해서 테스트할 엔터티를 생성하거나 해당 유저 정보를 조회하는 등의 작업을 수행해야 한다.

아래의 예시를 보자.

## Fixture
Fixture는 Enum으로 정의한다.
```java
public enum MemberFixture {

    ADMIN("violetbeach1", "관리자", "ti641924@gmail.com", UserRole.ADMIN, false),
    NORMAL("jaehoney2", "유저 닉네임", "oir232@gmail.com", UserRole.MEMBER, false),
    BLOCK("tistory13", "Test nickname", "325232@gmail.com", UserRole.MEMBER, true),
    
    ;

    private final String userId;
    private final String nickname;
    private final String email;
    private final UserRole userRole;
    private final boolean block;

    MemberFixture(String userId,
                  String nickname,
                  String email,
                  UserRole userRole,
                  boolean block) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.userRole = userRole;
        this.block = block;
    }
    
    public Member getMember() {
        return new Member(userId, nickname, email, userRole, block);
    }
    
}
```
이후에는 서로 다른 바운디드 컨텍스트를 테스트할 때 해당 Enum을 사용해서 Member를 생성할 수 있다.

즉, 재사용성이 증가한다.

만약 여기서 게시글 Fixture를 만든다면 아래와 같이 구현할 수 있다.

```java
public enum PostFixture {

    NOTICE("공지사항", "공지사항입니다.", PostType.NOTICE),
    CHAT("게시글", "게시글입니다.", PostType.CHAT)
    ;

    private final String title;
    private final String content;
    private final PostType type;

    PostFixture(String title, String content, PostType type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public Post getPost(Member author) {
        return new Post(
            author, title, content, type, PostStatus.ACTIVE);
    }

    public Post getPost(Member author, String status) {
        return new Post(
                author, title, content, type, PostStatus.valueOf(status));
    }
}
```
이후에 해당 데이터의 생성을 할 때는 아래와 같이 사용하면 된다.

`postRepository.save(NOTICE.getPost(member));`

엔터티 뿐만 아니라 자주 사용하는 DTO 등에도 해당 방법을 적용해서 다양한 바운더리 컨텍스트의 테스트를 깔끔하게 작성할 수 있다. 
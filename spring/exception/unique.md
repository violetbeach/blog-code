## Spring - Unique Key를 제어하는 방법 (비교)!

JPA를 사용하면 아래와 같이 엔터티를 구현합니다.

```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account implements Serializable {

    @Id
    private String id;

    private String password;

    private String name;

    @Column(unique = true)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    private AccountStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_role",
            joinColumns = { @JoinColumn(name = "account_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") }
    )
    private Set<AccountRole> roles;
    
    // ... 생략

}
```

여기서 DB의 phone 컬럼에 Unique Key가 걸려있습니다.

이제 이것을 코드단에서 풀어내는 방법은 크게 두 가지가 있습니다.

1. 비즈니스 로직에서 체크하는 방법
2. ConstraintViolationException을 핸들링하는 방법

## 비교

### 추가 쿼리

1번 방법인 비즈니스 로직에서 체크하는 방법의 경우에는 **의미 없는 쿼리**가 추가로 나간다는 단점이 있습니다.

예를 들어, Account를 생성하기 전에 생성할 유저의 phone과 동일한 데이터가 DB에 있는지 확인해야 합니다.

-> 즉, save를 통해 엔터티를 저장하기 전에 find를 통해 중복 데이터가 없는 지 추가로 확인해야 합니다.

2번 방법인 ConstraintViolationException을 핸들링하게 되면 그냥 Insert를 때리면 됩니다. 그러면 DB단에 걸려있는 Unieque Key 때문에 예외가 터지게 되고 해당 예외를 핸들링할 수 있습니다.

아래는 김영한님 강의에서 나온 QA에 대한 답변입니다.
> **김영한님 의견**: 애플리케이션 전체를 볼때 PK나 index에서 단건을 조회하는 경우 성능에 미치는 영향은 거의 미미합니다. 그리고 대부분의 애플리케이션은 조회가 많지, 이렇게 저장하는 비즈니스 로직의 호출은 상대적으로 매우 적습니다.  그래서 이 경우 성능은 크게 고려대상이 안됩니다.

### 동시성 문제

1번 방법은 추가로 동시성 문제가 생길 수 있습니다.

예를 들어 동일한 시점에 동일한 phone으로 두 명의 사용자가 동시에 생성 요청을 보내면 select 시점에는 둘다 통과되어서 insert가 날라가서 결국 DB단까지 도착해서 ConstraintViolationException가 터집니다.

### 에러 핸들링

그러면 2번 방법은 어떤 단점이 있을까요?

예외를 핸들링하기가 매우 까다롭습니다. ConstraintViolationException가 터졌지만 해당 예외가 Unique Key에 대한 것 때문인지, Foreign Key에 대한 것인지, Not Null 컬럼에 null을 넣은 것인지, 등... 원인을 알 수가 없습니다.

그렇다고 에러 메시지를 그대로 반환해주면 DB 구조가 노출됩니다.

### 권장하는 방법

이러한 이유로 1번 방식을 권장합니다.

1번 방식을 사용하면 체크 로직을 통해 원하는 상황을 깔끔하게 제어할 수 있습니다.

동시성 문제가 해결되지는 않지만 동시성 문제는 매우 드문 상황이고, 이 때는 공통 예외로 컨트롤러 끝까지 보내서 공통 예외로 처리되도록 만들면 됩니다. 

감사합니다.

## 참고
> https://www.inflearn.com/questions/59250

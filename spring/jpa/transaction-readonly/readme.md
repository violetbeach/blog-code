## Spring - @Transactional(readOnly = true)을 하면 일어나는 일

왜 Service 클래스 단위에 기본적으로 @Transactional(readOnly = true) 속성을 사용할까?

## 예상치 못한 엔터티의 등록, 변경, 삭제 예방

@Transactional에 readOnly = true 옵션을 주면 스프링 프레임워크가 세션 플러시 모드를 MANUAL로 설정한다.

이렇게 하면 강제로 플러시를 호출하지 않는 한 플러시가 일어나지 않게 되어서, 트랜잭션이 커밋되면서 실수로 엔터티가 등록, 수정, 삭제 되는 일을 방지한다. 

## 성능 최적화

[1]. 트랜잭션을 읽기 전용으로 열면 JPA에서 제공하는 스냅샷 저장이나 변경 감지 등을 사용하지 않아서 자원(CPU, Memory)의 낭비를 줄일 수 있다.

[2]. 추가로 DBMS 별로 다르지만, MySQL의 경우 Read Only 트랜잭션에 대해서는 트랜잭션 ID를 부여하지 않는다.

-> Transaction ID 설정에 대한 오버헤드를 줄일 수 있다.

## Replication 적용

DB 서버가 Master, Slave로 분리되어 있다면, 아래와 같이 트랜잭션의 readOnly 옵션에 따라 Slave DB 서버를 호출하게 처리할 수 있다.

```java
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) ? "slave" : "master";
}
```

즉, DB Replication을 처리하기가 수월해진다. 

## 참고
- https://willseungh0.tistory.com/75
- https://junhyunny.github.io/spring-boot/jpa/junit/transactional-readonly/
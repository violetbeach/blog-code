## JPA - 비관적 락, 낙관적 락 (+ 트랜잭션 격리 수준) 정리!

### 격리 수준

락(Lock)을 이해하기 전에 트랜잭션 격리 수준을 먼저 알아야한다.

트랜잭션 격리수준(isolation level)이란 동시에 여러 트랜잭션을 처리할 때, 트랜잭션이 얼마나 서로 고립되어 있는지를 의미한다. 즉, **해당 트랜잭션이 다른 트랜잭션에서 변경한 데이터를 볼 수 있는 기준**을 결정하는 것이다.

격리수준은 크게 4가지로 나눌 수 있다.

-   READ UNCOMMITTED
    -   커밋되지 않은 데이터를 읽을 수 있다.
    -   Dirt Read, Dirty Write가 발생할 수 있다.
-   READ COMMITTED
    -   커밋된 데이터만 읽을 수 있다.
    -   오라클 DBMS에서 표준으로 사용하고 있고 가장 많이 선택된다.
    -   Lost Update, Write Skew, Read Skew가 발생할 수 있다.
-   REPETABLE READ
    -   MySQL에서는 트랜잭션마다 트랜잭션 ID를 부여하여 트랜잭션 ID보다 작은 트랜잭션 번호에서 변경한 것만 읽게 된다.
    -   Undo 공간에 백업해두고 실제 레코드 값을 변경한다.
    -   즉, 트랜잭션 동안 같은 데이터를 읽을 수 있다.
    -   Phantom Read가 발생할 수 있다.
        -   다른 트랜잭션에서 수행한 변경 작업에 의해 레코드가 보였다가 안 보였다가 하는 현상
        -   REPEATABLE READ는 변경(Update)는 제어하지만, INSERT는 제어할 수 없기 때문
-   SERIALIZABLE
    -   모든 트랜잭션을 순서대로 실행한다.

트랜잭션 격리 수준이 높아질수록 자원을 많이 사용하고 성능이 떨어진다. 일반적으로는 READ COMMITTED나 REPEATABLE READ 중 하나를 사용한다.

JPA를 사용하면 READ COMMITTED 이상의 격리 수준이 필요할 때 비관적 락, 낙관적 락을 선택해야 한다.

### 비관적 락, 낙관적 락?

-   비관적 락(Pessimistic Lock)
    -   트랜잭션이 충돌한다고 가정하고 락을 건다.
    -   DBMS의 락 기능을 사용한다. (ex. SELECT FOR UPDATE)
    -   데이터 수정 시 즉시 트랜잭션 충돌여부를 확인할 수 있다.
-   낙관적 락(Optimistic Lock)
    -   트랜잭션이 충돌하지 않는다고 가정한다.
    -   자원에 락을 걸어서 선점하지말고 **커밋할 때 동시성 문제가 발생하면 그때 처리** 하자는 방법론입니다.
    -   JPA에서는 자체적으로 제공하는 **버전 관리** 기능을 사용한다. (hashcode나 timestamp를 이용할 수도 있다.)
    -   트랜잭션을 커밋하기 전까지는 충돌 여부를 확인할 수 없다.

### 비관적 락

SpringDataJPA에서 @Lock 어노테이션을 사용해서 비관적 락을 걸 수 있다.

```
public interface UserRepository extends JpaRepository<User, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from User b where b.id = :id")
    User findByIdForUpdate(Long id);
    
}
```

추가적으로 Row JPA를 사용하고 있다면 EntityManager를 통하여 직접 락을 지정할 수 있다.

```
entityManager.lock(user, LockModeType.OPTIMISTIC);
```

JPA에서 비관적 락을 구현하는 LockModType의 종류는 아래와 같다.

-   PESSIMISTIC\_READ
    -   다른 트랜잭션에서 읽기만 가능
-   PESSIMISTIC\_WRITE
    -   다른 트랜잭션에서 읽기도 못하고 쓰기도 못함
-   PESSIMISTIC\_FORCE\_INCREMENT
    -   다른 트랜잭션에서 읽기도 못하고 쓰기도 못함 + 추가적으로 버저닝을 수행한다.

비관적 락은 조회한 레코드 자체에 락을 걸기 때문에 성능이 저하될 수 있다.

성능상 이슈가 발견된다면 낙관적 락을 고려해야 한다.

### 낙관적 락

SpringDataJPA에서 낙관적 락을 사용하기 위해서는 @Version을 엔터티에 추가해서 사용하면 된다.

```
@Entity
// Lombok 생략
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;
    
}
```

이제 Entity를 수정할 때마다 JPA가 자체적으로 Versioning을 지원하기 때문에 조회 시점과 수정 시점이 다르면 `ObjectOptimisticLockingFailureException` 예외가 발생한다.

JPA에서 낙관적 락을 구현하는 LockModType의 종류는 아래와 같다.

-   OPTIMISTIC
    -   트랜잭션 시작 시 버전 점검이 수행되고, 트랜잭션 종료 시에도 버전 점검이 수행된다.
    -   버전이 다르면 트랜잭션이 롤백된다.
-   OPTIMISTIC\_FORCE\_INCREMENT
    -   낙관적 락을 사용하면서 추가로 버전을 강제로 증가시킨다.
    -   관계를 가진 다른 엔터티가 수정되면 버전이 변경된다. (ex. 댓글이 수정되면 게시글도 버전이 변경된다.)
-   READ
    -   OPTIMISTIC과 동일하다.
-   WRITE
    -   OPTIMSTIC\_FORCE\_INCREMENT와 동일하다.
-   NONE
    -   엔터티에 @Version이 적용된 필드가 있으면 낙관적 락을 적용한다.

JpaRepository를 사용한다면 @Lock 어노태이션의 LockModeType를 지정할 수 있다.

```
@Lock(LockModeType.OPTIMISTIC)
Optional<User> findByIdForUpdate(Long id);
```

---

### 정리

@Transactional의 isolation level(격리 수준)의 JPA Repository Method의 @Lock은 아래의 차이가 있다.

-   Isolation level(격리 수준)은 해당 트랜잭션이 다른 트랜잭션에서 변경한 데이터를 볼 수 있는 기준을 정의한다.
-   Lock(잠금)은 다른 트랜잭션에서 해당 데이터에 접근하는 것을 막는 기능을 수행한다.

낙관적 락은 일반적으로 처리 요청을 받은 순간부터 처리가 종료될 때까지 레코드를 잠그는 비관적 락보다 성능이 좋다.

데이터 성향에따라, 비관적 락이 좋은 경우도 있는데 이런 경우이다.

-   재고가 1개인 상품이 있다.
-   100만 사용자가 동시적으로 주문을 요청한다.

비관적 락의 경우 1명의 사용자 말고는 대기를 하다가 미리 트랜잭션 충돌 여부를 파악하게 된다. 즉, 재고가 없음을 미리 알고 복잡한 처리를 하지 않아도 된다.

낙관적 락의 경우 동시 요청을 보낸 사용자가 처리를 순차적으로 하다가 Commit을 하는 시점에 비로소 재고가 없음을 파악하게 된다. 그리고 처리한 만큼 롤백도 해야하기 때문에, 자원 소모도 크게 발생하게 된다.
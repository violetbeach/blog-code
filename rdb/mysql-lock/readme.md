# MySQL - 락 불필요한 데이터를 잠그는 문제 정리! (+ 트랜잭션과 락의 차이)

락은 DBMS이나 애플리케이션에서 동시성을 제어할 수 있는 방법이다.

해당 포스팅에서는 MySQL의 락에 대해 다룬다.

## 락 이란?

락을 통해 동시성을 제어할 때는 락의 범위를 최소화하는 것이 중요하다.

- 락의 범위가 길어지면 대기중인 DB 커넥션이 많아지므로 커넥션 풀 고갈로 이어질 수 있다.

MySQL에서는 트랜잭션의 커밋 혹은 롤백시점에 잠금이 풀린다. 즉, 트랜잭션이 곧 락의 범위가 된다.

## 트랜잭션과 락

예시를 통해 알아보자.

한 트랜잭션 내에서 DB에 Update를 하고 새로운 이미지를 업로드한다고 한다고 가정하자.

트랜잭션과 락은 각각 아래의 역할을 수행한다.

- **트랜잭션**
  - 업로드가 진행되는 동안에도 DB 커넥션을 유지하고 트랜잭션을 지속한다. 
  - 업로드가 성공하면 트랜잭션을 커밋한다. 
  - 업로드가 실패하면 진행중이던 DB 트랜잭션을 롤백한다.
- **락**
  - 업로드가 진행되는 동안 다른 커넥션에서 데이터를 참조되는 것을 막는다.

즉, 트랜잭션과 락의 범위는 모두 DB 커넥션 풀에 영향을 미친다. 트랜잭션은 DB 커넥션을 지나치게 많은 시간을 점유하게 하고, 락은 다른 DB 커넥션이 접근하지 못하게 대기시킨다는 점에서 차이가 있다.

```
Q. Insert 시에는 락이 어떻게 동작할까?
 - 해당 질문이 예상될 수 있다.
 
A. Insert를 할 때도 락은 중요하다.

개발을 할 때 Transaction 안에서 Entity를 생성하고, /static/{entityId}에 파일을 저장한다고 가정하자.

1. Entity를 생성하면 AutoIncrement로 인해 id(1)를 할당받는다.
2. 해당 id(1)를 사용해서 /static/1에 파일을 저장한다.

해당 Entity는 아직 commit 되지 않았다. 이 때 다른 서버에서 Entity 생성 요청이 들어오면 어떻게 될까..?
id를 1로 생성할까? 그렇게 되면 이전 요청은 실패해야 한다!

이전에는 갭 락(Gap Lock)을 사용해서 다른 데이터가 Insert가 생성되지 못하도록 막아서 처리했다.

현재는 Table 단위의 AUTO_INC LOCK을 사용한다.
Transaction 내부를 포함한 모든 Insert 문마다 AutoIncrement를 수행한다.
그래서 Insert가 실패 시에도 AutoIncrement가 증가한다. 
```


## 읽기 락과 쓰기 락

락은 읽기 락(Shared Lock)과 쓰기 락(Exclusive Lock)으로 나눌 수 있다. 각 락은 아래의 특징을 가진다.

읽기 락(Shared Lock)이 걸려있으면
- 읽기 락은 접근이 가능하다.
- 쓰기 락은 접근이 불가능하다.

쓰기락(Exclusive Lock)이 걸려있으면
- 읽기 락은 접근이 불가능하다.
- 쓰기 락도 접근이 불가능하다.

즉, 읽기 락끼리는 공유가 가능하지만 다른 경우들은 모두 데이터를 공유할 수 없고 대기를 하게 된다고 생각하면 된다.

MYSQL에서 읽기 락은 SELECT ... FOR SHARE 구문을 사용하고,

쓰기락은 SELECT ... FOR UPDATE 또는 UPDATE, DELETE 쿼리 시 획득할 수 있다.

## Lock의 범위

MySQL에서 잠금은 row가 아니라 **인덱스**를 잠근다.

그래서 인덱스가 없는 조건으로 Locking Read시 불필요한 데이터들이 잠길 수 있다.

예를 들어 아래의 쿼리를 실행한다고 가정해보자.

```sql
start transaction;
SELECT * FROM post WHERE memberId = 1 AND contents = 'string' FOR UPDATE;
```

해당 DB의 전체 record는 아래와 같다.

WHERE 조건 일부인 contents 컬럼이 'string'인 데이터는 현재 존재하지 않는다.

**여기서 만약 contents 컬럼에 인덱스가 없다면 어떻게 될까?!!**

![img_1.png](img_1.png)

아래의 락 조회 문을 사용할 수 있다. (위에서 COMMIT을 수행하지 않았으므로 락을 유지하고 있을 것이다.)

```sql
# 실행중인 락 조회
SELECT * FROM performance_schema.data_locks;
# 실행중인 트랜잭션 조회
SELECT * FROM performance_schema.innodb_trx;
```
 
![img.png](img.png)

LOCK_MODE가 X라는 것은 쓰기 락이 잡혔다는 것이다.

문제는 LOCK_DATA 부분이다. (index column, pk column 구조)

해당을 보면 memberId가 1인 모든 컬럼에 락이 잡힌 것을 알 수 있다.

앞서 언급했듯 MySQL에서 Lock은 **Index**를 잠근다고 했다.

Index에 contents가 포함되지 않았기 때문에 memberId 컬럼 조건을 대상으로 하는 레코드 락을 걸어버린 것이다!

#### 주의

만약 Index가 없는 조건 절로 락을 걸면 전체 데이터가 잠겨버린다.
- 이 때는 어떤 조건으로도 데이터 조회가 불가능하다.

그래서 MySQL을 사용할 떄 인덱스를 효율적으로 사용하는 것이 굉장히 중요하다.

## 참고

- https://stuffdrawers.tistory.com/11
- https://idea-sketch.tistory.com/46
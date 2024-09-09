## Fetch Join의 원리

JPA의 N+1 문제를 해결하고자 Fetch Join을 사용한다고 잘 알려져있다.

그러면 N+1은 왜 발생하고 Fetch Join은 어떤 방식으로 이 문제를 해결한 걸까..?

## N+1

#### N+1은 언제 발생할까?

- N+1은 1:N 관계를 가진 엔터티를 조회할 때 발생한다.
- EAGER 전략으로 조회하던 LAZY 전략으로 조회하던 발생한다.

#### N+1이 발생하는 이유

JPA Repository로 find를 할 때 하위 엔터티까지 한 번에 가져오지 않는다. JPQL로는 첫 엔터티만 조회하고, 이후 JPA를 사용해서 하위 엔터티를 추가로 조회한다.

이때 N+1 문제가 발생한다.

## Fetch Join

FetchJoin을 사용하면 최초 JPQL로 Innser Join을 해서 데이터를 전부 가져온다.

## EntityGraph

`@EntityGraph`를 사용하면 쿼리를 작성하지 않아도 N+1 문제를 해결할 수 있다.

하지만 `Inner Join`이 아니라 `Outer Join`으로 동작하기 때문에 데이터가 많이 생길 수 있다.
## MongoDB - 다양한 인덱스(Index) 정리

MongoDB는 기존 RDB와 다르게 다양한 종류의 인덱스(Index)를 지원한다.

그래서 다양한 질의가 필요한 경우에 MongoDB가 적합한 솔루션이 될 수 있다.

이번 포스팅에서는 MongoDB의 인덱스들이 어떤 역할을 하는 지 알아보자.

## 1. Single Field Index(단일 필드 인덱스)

하나의 필드 인덱스를 사용하는 것을 의미한다. (MongoDB에는 기본적으로 컬렉션에 _id라는 단일 필드 인덱스가 생성된다.)

단일 필드 인덱스에서는 오름차순 / 내림차순 여부가 중요하지 않다.

```mongodb-json-query
> db.user.createIndex({name:1})
```

참고로 1은 오름차순, -1은 내림차순을 의미한다.

## 2. Compound Index(복합 인덱스)

두 개 이상의 필드를 사용하는 인덱스를 복합 인덱스라고 한다.

```mongodb-json-query
db.user.createIndex({userid:1 ,name:-1})
```

복합 인덱스를 사용할 때는 몇가지 주의할 점이 있다.

1. 정렬 시 우선 순위를 고려해야 한다.

MongoDB도 대부분의 RDBMS와 동일하게 Index 자료구조로 B-TREE를 사용한다. 그래서 인덱스의 순서가 정렬 순서와 다르다면 Index를 사용할 수 없다.

당연히 첫 번째 필드를 제외하고, 두 번째 필드로 정렬을 하는 경우에도 동작할 수 없다.

2. 정렬 방향을 고려해야 한다.

위와 유사한 문제인데, B-TREE를 사용하기 떄문에 생성된 인덱스의 정렬이 완전하게 역방향이라면 지원이 가능하지만, 일부만 방향이 다른 경우 지원하지 않는다.

```mongodb-json-query
- index: {a: 1, b: -1}

- possible_index_query: { a: 1, b: -1 }
- possible_index_query: { a: -1, b: 1 }

- bad_query: { b: 1, a: 1 }
- bad_query: { b: -1, a: -1 }
```

## 참고
- https://ryu-e.tistory.com/1
- https://freeend.tistory.com/102
- https://inpa.tistory.com/entry/MONGO-%F0%9F%93%9A-Index-%EC%A0%95%EB%A6%AC

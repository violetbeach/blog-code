## MongoDB driver

MongoDB 회사 에서 공식적으로 2가지 드라이버를 제공한다.
- Java Driver (Sync)
- Reactive Streams Driver (Async)

#### Java driver (Sync)

Java Driver는 동기로 동작하는 애플리케이션을 위한 드라이버이다.

![img.png](img.png)

클라이언트는 요청을 보내면 응답이 돌아오기 전까지 쓰레드가 Blocking된다.

코드가 직관적이고 작성 난이도가 낮지만, 처리량이 비교적 높지 않다.

## Reactive Streams Driver

Reactive Streams driver는 비동기로 동작하는 애플리케이션을 위한 MongoDB 드라이버이다.

![img_1.png](img_1.png)

해당 드라이버에서는 먼저 쿼리가 잘 전달되었다는 응답을 받는다. 실제 결과 응답은 이후 시점에서 받게 된다.

## Spring Data Mongo

#### Entity

Spring Data Mongo에서 Entity는 `@Entity`가 아닌 `@Document` 애노테이션을 붙인다.

그리고 JPA와 다르게 필드에 `@Column` 애노테이션이 아니라 `@Field` 애노테이션을 붙인다.

#### ReactiveMongoTemplate

- ReactiveMongoOperations를 구현한다.
- 메서드 체이닝을 통해 쿼리를 수행하고 결과 Entity를 반환한다.

#### MongoTemplate

- MongoClient로 생성할 수 있다.

#### Id Mapping

MongoDB의 모든 document는 `_id`를 필요로 한다. MappingMongoConverter는 다음의 방법으로 `_id`를 감지한다.
- `@Id`가 붙어있는 필드
- 필드명이 id이고 `@Field`로 별도 이름이 부여되지 않은 경우

해당 경우가 존재하지 않으면 자동으로 `_id`를 추가한다.

## Property population

R2dbc에서는 Mutable일 때는 reflection을 이용한 Property population을 적용했다.

Immutable인 경우는 with* 메서드를 사용해서 새로운 객체를 생성한다.

객체 생성 절차는 아래와 같다.
1. mutable 필드 Reflection으로 호출
2. Immutable 필드 with* 메서드 호출

## Index

인덱스는 `@Indexed`로 사용해서 생성할 수 있다. 기본적으론느 자동 생성이 비활성화이므로 별도 생성이 필요하다.

DB 쿼리로 인덱스를 직접 생성하는 것이 더 권장된다.

## PersistenceConstructor

`@PersistenceConstructor` 특정 constructor에 대해 Object creation 할 때 사용하게 지정할 수 있다.

## ReactiveMongoTemplate

#### ReactiveFindOperation

matching
- query의 filter에 해당
- Query를 전달하여 filter에 들어갈 내용을 설정

최종
- 마지막으로 count, exists, first, one, all, tail 등의 연산을 선택
  - count:조건에 맞는 document 개수 반환
  - exists: 조건에 맞는 document 존재 여부 반환
  - first: 조건에 맞는 첫 번째 document 반환
  - one: 조건에 맞는 하나의 document 반환. 하나가 넘으면 exception
  - all: 조건에 맞는 모든 document 반환
  - tail: cursor를 이용하여 조건에 해당하는 document를 지속적으로 수신

#### ReactiveInsertOperation

one: Insert Query에 필요한 Entity 하나를 전달
- 주어진 Entity를 Document로 변환하고 Insert
- 결과를 Mono로 반환

all: bulk insert를 지원
- 주어진 Entity Collection을 Document Collection으로 변환하고 Insert
- 결과를 Flux로 반환

#### ReactiveUpdateOperation

Spring Data Mongo는 update 메서드는 아래와 같다.
- update
  - Entity가 아닌 Update 객체를 전달
  - set, unset, setOrInsert, inc ,push, pop, pull, rename, multiply 등의 연산 지원
  - 조건: all, first, upsert

update는 Atmoic한 기능 제공을 위한 Options를 제공한다.
- findAndModify
  - Document를 찾은 후 다른 Document로 대체
  - Option - returnNew: 대체된 Document를 반환 / 기존 Document를 반환
  - Option - upsert: 조건에 만족하는 Document가 없는 경우 insert
  - Option - remove: 값이 존재하면 삭제
- findAndReplace
  - Document를 찾은 후 다른 Document로 대체
  - Option - returnNew: 대체된 Document를 반환 / 기존 Document를 반환
  - Option - upsert: 조건에 만족하는 Document가 없는 경우 insert

#### ReactiveRemoveOperation

ReactiveRemoveOperation은 내부적으로 `findAndRemove`를 사용하기 때문에 삭제 건을 반환하는 게 아니라, 삭제한 Document를 반환할 수 있다.

#### ReactiveAggregationOperation

#### ReactiveChangeStreamOperation

특정 Document를 대상으로 filter나 as를 걸어서 listen을 할 수 있다.

내부적으로 resumeAt, resumeAfter, startAfter 등을 사용해서 특정 시점의 데이터를 가져올 수도 있다.
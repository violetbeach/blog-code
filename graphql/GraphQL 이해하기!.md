GraphQL 이란 2012년 페이스북의 클라이언트 데이터 전송 방식 개선 목적으로 시작되었다.

GraphQL이란 클라이언트와 서버의 통신 **명세**이다. (REST와 마찬가지로 실체는 없다.)
- 명세를 기반으로 다양한 라이브러리가 생성되었다.

구조(스키마)와 행동(리졸버)로 구성된다.

## vs REST API

GraphQL과 REST API의 차이를 알아보자.

|     | GraphQL                       | REST           |
|-----|-------------------------------|----------------|
| 구성  | 스키마 / 타입 시스템                  | URL endpoints  |
| 동작  | Query, Mutation, Subscription | CRUD           |
| End-point | 단일 접점(API 1개)                 | URL 집합         |
|데이터 포맷| Only JSON                     | JSON, XML, ... |
|관점| 클라이언트 주도 설계                   | 서버 주도 설계       |
|러닝 커브| 어려움                           | 보통 (비교적 쉬움)    |

그러면 이러한 차이는 어떤 장점이 있을까?
- 클라이언트에게 많은 제어권을 넘길 수 있다. (필요한 필드 목록 등을 클라이언트에서 주도할 수 있다.)
- 동일한 스펙(Query)으로 여러 서버에 질의할 수 있다.
- 오버페칭, 언더페칭이 적다.
- UI에 의존적이지 않고, 도메인 중심적으로 설계가 가능하다.
- 상대적으로 빠르다.

이제 위 특징들을 하나씩 살펴보자.

#### 클라이언트 주도 설계

클라이언트 측은 사용자의 이름을 다음과 같이 질의할 수 있다.

- request:`{ hero { name } }`
- response: `{ "hero": { "name": "Luke Skywalker" } }`

이때 이름 뿐만 아니라 키와 머리색도 질의하고 싶다면 다음과 같이 질의할 수 있다.

- request: `{ hero { name height hairColors } }`
- response: `{ "hero": { "name": "Luke Skywalker", "height": 1.72, "hairColors": ["black", "brown"] } }`

즉, 클라이언트에서 필요한 필드를 주도적으로 조회할 수 있게 된다.

#### 단일 접점

GraphQL은 API end-point가 하나라서 클라이언트 입장에서 사용하기 편리하다.

#### 오버페칭, 언더페칭

만약 사용자 한명의 많은 필드 중에서 주소만 조회하고 싶은 경우가 있다.

REST의 경우 `GET /사용자/id/주소`가 존재하지 않는다면 GET `/사용자`로 조회를 해야할 것이다. 이때 오버페칭이 발생한다.

반대로 클라이언트의 처리에 필요한 데이터가 부족한 경우도 종종생겨서, 추가 작업을 해야하는 경우가 생긴다. 즉, 언더페칭이 발생한다.

GraphQL을 사용하면 이를 해결할 수 있다.

단, 이러한 요구사항에 맞게 스키마 설계가 되어야 하고 리졸버를 잘 구현하는 것이 필요하다.

### GraphQL 단점

GraphQL도 당연히 장점만 존재하지는 않는다.
- GraphQL 스키마의 유지보수가 필요하다.
  - 스키마 설계
  - 트리 그래프의 깊이 제어
  - ...
- 캐싱 전략을 적용하기 어렵다. (쿼리에 따라 응답이 매우 동적이기 때문)
- 학습 곡선이 크다.

### 문서화

REST API의 경우 Swagger 도구 등을 활용해서 API 스펙을 문서화해야 한다.

GraphQL은 스키마 자체가 API 문서이기 때문에 별도로 문서화를 할 필요가 없다.

## 개발 방식

GraphQL 개발 방식은 아래의 두 가지 방식이 있다.

- Schema First - 스키마를 먼저 정의 -> 코드 작성
- Code First - 코드 작성 -> 스키마 자동 생성

일반적으로는 클라이언트 주도적으로 설계할 수 있고, 추후 변경 비용이 적은 SchemaFirst 방식을 권장한다.

## 자바 프레임워크

JVM & 스프링 기반의 서버에서는 아래의 도구를 활용할 수 있다.
- Spring for GraphQL
- Netflix DGS
- GraphQL Kick Starter

## GraphQL 쿼리어

GraphQL의 쿼리어는 문법에 따라 3가지로 나뉜다.
- query - 조회
- mutation - 입력, 수정, 삭제
- subscription - 구독

### 1. Query

Query는 조회, 질의를 할 때 사용한다.

스키마에 아래와 같이 쿼리 타입을 정의할 수 있다.

```bash
type Query {
    shows: [Show]
}

type Show {
    title: String,
    actors: [Actor]
}
```

여기서 Key는 클라이언트 요청에 의해 질의할 데이터이고, Value는 응답이 된다.

### 2. Mutation

Mutation은 생성, 수정, 삭제 시 사용한다.

```bash
type Mutation {
    addRating(title: String, stars: Int):Rating 
}

type Rating {
    avgStars: Float
}
```

Query와 마찬가지로 응답이 Value에 해당한다.

### 3. Subscription

Subscription은 클라이언트 측에서 서버의 이벤트를 구독하고 처리할 때 사용한다.

```bash
type Subscription {
    commentAdded(postId: ID!):Comment 
}
```

## GraphQL 필드

### 1. 타입

GraphQL은 아래 타입을 지원합니다.
- Scalar(Int, Float, String, Boolean, ID, Custom ..)
- Object
- Input
- Enum
- Interface
- Union
- Fragment

아래는 Custom Scalar의 경우 아래와 같이 표현할 수 있다.

```bash
type Query {
    products(page: Int, size: Int): [Product]
}

type Product {
    productId: ID
    name: String
    price: BigDecimal
    vendorId: Int
    status: ProductStatus
    imageUrl: String
    imageDetailUrl: String
    productDesc: String
    isExposed: Boolean
    isDeleted: Boolean
    createdAt: DateTime
    createdBy: String
    updatedAt: DateTime
    updatedBy: String
}

enum ProductStatus {
    READY_TO_SELL
}

scalar BigDecimal
scalar DateTime
```

BigDecimal과 DateTime과 같은 커스텀 스칼라를 선언하고, 타입으로 사용할 수 있다. 

### 2. Null, 배열 표현

필드(파라미터, 응답 값)를 표현할 때는 크게 보면 두 가지만 알면 된다.
- null 규칙을 `!`로 표현한다.
- 배열을 표현할 때는 []를 사용한다.

null 규칙의 경우 아래를 참고하자.

| Expression | Description             |
|:-----------|:------------------------|
| String     | 널을 허용한다.                |
| String!    | 널을 허용하지 않는다.            |
| [String!]! | 리스트와 요소 모두 널을 허용하지 않는다. |

예를 들어 Product 질의 시 id에 null을 넣을 수 있고, null이 반환될 수도 있으면 아래와 같이 작성한다.

```bash
type Query {
    Product(id: ID!): Product!    
}
```

### 3. 기본값 표현

기본 값의 경우 아래와 같이 표현할 수 있다.

```bash
type Query {
    products(pages: Int=1 size: Int=10): [Product!]!
}
```

## GraphQL 주의사항

GraphQL을 사용할 때 고려할 점은 다음과 같다.
- 데이터 양의 제어
- 깊이 제어
- 쿼리 복잡도 제어
- 클라이언트 사이드의 커넥션 타임아웃

REST API도 마찬가지지만, 너무 많은 데이터를 한 번에 요청하면 서버가 죽을 수 있다.

그리고 트리 형태의 데이터를 내려주므로 부하와 사용성의 Trade off 발생한다.

결과적으로 사용자(클라이언트)의 사용성과 서버 부하를 적절히 조율하고 제어하는 것이 필요하다.

## 참고
- https://www.redhat.com/ko/topics/api/what-is-graphql
- https://graphql-kr.github.io/learn/queries
- https://chanhuiseok.github.io/posts/gql-3
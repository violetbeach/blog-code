![img.png](img.png)

테스트를 위한 객체를 생성하는 패턴이나 라이브러리가 필요하게 되었고, 훌륭하신 팀원 분이 FixtureMonkey를 추천해주셔서 POC를 진행하게 되었다.

## FixtureMonkey

**FixtureMonkey**는 **Naver**에서 만든 테스트 객체를 쉽게 생성하고 조작할 수 있도록 도와주는 Java 및 Kotlin 라이브러리이다.

네이버에서 만들었기 때문에 한국어 docs를 지원한다. 그래서 블로깅을 하는 게 큰 의미를 가지지는 않고, 요약 및 검토 정도로 봐주면 좋을 것 같다.
- https://naver.github.io/fixture-monkey/v1-0-0-kor/docs/introduction

## 사용하지 않았던 이유

예전에 해당 라이브러리르 한번 봤었고, 적용한다는 블로그를 많이 봤다. 하지만 버전과 Starts(인지도)가 모두 Minor 하다고 판단해서 적용하지 않기로 했었다.

추가로 아래 부분에 대한 의문이 해소되지 않았었다.
- 객체를 검증을 통과할 수 있는 상태로 생성하기 위해 코드가 복잡할 것 같음
  - 검증 로직과의 동기화 필요
- 테스트가 멱등하지 않을 것 같다.
- 가독성이 좋지 않을 것 같다. (코드가 짧아지지만, 의미를 노출하지 못할 것 같다.)

최근에 인기가 급상승 하고 있기도 하고, 요청도 있으니 한번 검토를 해보자!

## 컨셉

FixtureMonkey는 공식적으로 4가지 컨셉을 언급한다.

#### 1. 간결함

테스트 객체 생성이 매우 간단해진다. 

```java
Product actual = fixtureMonkey.giveMeOne(Product.class);
```

#### 2. 재사용성

여러 테스트에서 빌더를 재사용할 수 있다.

```java
ArbitraryBuilder<Product> actual = fixtureMonkey.giveMeBuilder(Product.class)
    .set("id", 1000L)
    .set("productName", "Book");
```

#### 3. 랜덤성

무작위 값으로 테스트 객체를 생성하므로 엣지 케이스를 발견할 수 있다. 

```java
ArbitraryBuilder<Product> actual = fixtureMonkey.giveMeBuilder(Product.class);
then(actual.sample()).isNotEqualTo(actual.sample());
```

#### 4. 다용도성

상속 / 순환 참조 / 익명 객체 등 다양한 경우에서 모두 동작한다.

```java
interface Foo {
    Bar getBar();
}

Foo foo = FixtureMonkey.create().giveMeOne(Foo.class);
```

1. 간결함
2. 재사용성
3. 랜덤성
4. 다용도성

## 사용 방법

#### giveMeOne

특정 타입의 인스턴스가 필요하다면 `giveMeOne()`을 사용한다.

```kotlin
val product: Product = fixtureMonkey.giveMeOne()
val strList: List<String> = fixtureMonkey.giveMeOne()
```

#### giveMe

여러 개의 인스턴스가 필요하다면 `giveMe()`을 사용한다.

```kotlin
val productList: List<Product> = fixtureMonkey.giveMe(3)
val productSequence: Sequence<Product> = fixtureMonkey.giveMe()
```

#### giveMeBuilder

인스턴스를 커스텀할 경우 `giveMeBuilder()`를 사용한다.

```kotlin
val productBuilder: ArbitraryBuilder<Product> = fixtureMonkey.giveMeBuilder()
```

해당 빌더는 아래와 인스턴스를 생성하는 데 사용할 수 있다.

```kotlin
val product = productBuilder.sample()
val productList = productBuilder.sampleList(3)
val productStream = productBuilder.sampleStream()
```

해당 빌더를 잘 정의해서 const화 시키면 유효한 여러가지 Case의 객체를 손쉽게 만들 수 있을 것으로 보인다.

## 생성자 / 팩토리 메서드

생성자 오버로딩, 정적 팩토리 메서드 등 다양한 방식으로 클래스를 정의했을 수 있다.

이 경우 아래와 같이 메서드를 지정할 수 있다.

```kotlin
// 생성자 지정
 val product = fixtureMonkey.giveMeBuilder<Money>()
     .instantiateBy { 
         constructor<Money> { 
             parameter<Long>() 
         } 
     }
    .sample()

// 생성자 사용할 필드 지정
val product2 = fixtureMonkey.giveMeBuilder<Money>()
    .instantiateBy {
        constructor<Money> {
            parameter<Long>("amount")
        }
    }
    .sample()

// 팩토리 메서드 지정
val product3 = fixtureMonkey.giveMeBuilder<Money>()
    .instantiateBy {
        factory<Money>("from")
    }
    .sample()
```

## 유효성 검증 동기화

FixtureMonkey는 `jakarta.validation.constraints` 기반의 어노테이션을 지원한다.

아래와 같은 엔터티 객체가 있다.

```java
@Value
public class Money {
    @Min(0)
    long amount;
}
```

아래 의존을 추가한다.
```
testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation:1.0.14")
```

그러면 해당 Validation을 통과하는 범위의 객체를 생성할 수 있다.

```java
@Test
void test() {
    // given
    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
        .plugin(new JakartaValidationPlugin())
        .build();

    // when
    Product actual = fixtureMonkey.giveMeOne(Money.class);

    // then
    then(actual).isNotNull();
    then(actual.getPrice()).isMoreThanOrEqualTo(0);
}
```

생성자의 `verifyAmount()`와 같은 메서드를 통과할 때는 사용하기 어려울 것 같다.

## 객체 생성 Rule 적용

특정 단위 테스트에 필요한 객체를 만들 때는 Builder의 `set()`을 사용할 수 있다.

```java
@Test
void test() {
    // given
    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
        .build();
    long amount = 10000;

    // when
    Product actual = fixtureMonkey.giveMeBuilder(Money.class)
        .set("amount", amount)
        .sample();

    // then
    then(actual.getAmount()).isEqualTo(1000);
}
```

보통 도메인에서 Setter의 가시성을 닫기 때문에, 내부적으로 Reflection을 사용한다. 그래서 문자열로 필드명을 주게 되는데, 필드명이 바뀌게 되면 테스트가 깨질 것이라서 다소 아쉬운 것 같다.

단, 코틀린을 사용하면 다르다. 코틀린에서는 `setExp()`로 프로퍼티를 참조해서 커스텀한 객체를 생성할 수 있다.

```kotlin
@Test
fun test() {
    // given
    val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build();
  
    // when
    val actual = fixtureMonkey.giveMeBuilder<Money>()
        .setExp(Product::amount, 1000L)
        .sample()
  
    // then
    then(actual.amount).isEqualTo(1000L)
}
```

즉, 코틀린을 사용하면 필드명이 변경되어도 테스트에도 반영하기가 쉽다.

#### size

`size()`를 사용하면 프로퍼티의 크기를 지정할 수 있다. 최소값이나 최대값 지정도 가능하다.

```kotlin
fixtureMonkey.giveMeBuilder<Product>()
    .sizeExp(Product::options, 5) // size:5

fixtureMonkey.giveMeBuilder<Product>()
    .sizeExp(Product::options, 3, 5) // minSize:3, maxSize:5

fixtureMonkey.giveMeBuilder<Product>()
    .minSizeExp(Product::options, 3) // minSize:3

fixtureMonkey.giveMeBuilder<Product>()
    .maxSizeExp(Product::options, 5) // maxSize:5
```

#### Nullable

특정 필드를 null로 설정하거나, 항상 값이 존재하도록 보장하기 위해서는 아래와 같이 `setNull()`, `setNotNull()`을 사용할 수 있다.

```kotlin
fixtureMonkey.giveMeBuilder<Product>()
    .setNullExp(Product::id)

fixtureMonkey.giveMeBuilder<Product>()
    .setNotNullExp(Product::id)
```

#### Post Condition

복잡한 비즈니스 설정의 경우 아래와 같이 `Predicate`를 전달해서 조건을 만족하는 Fixture 객체를 생성할 수 있다.

```kotlin
fixtureMonkey.giveMeBuilder(Product::class.java)
    .setPostConditionExp(Product::id, Long::class.java) { it: Long -> it > 0 }
```

#### thenApply

특정 타입에 따라 달라야하는 필드도 있을 수 있다. `thenApply()`를 사용하면 이를 해결할 수 있다.
```kotlin
fixtureMonkey.giveMeBuilder(Product::class.java)
    .thenApply{it, builder -> builder.setExp(Product::name, it.type.toString())}
```

#### acceptIf

특정 조건에 따라 필드를 조정할 경우 `acceptIf()`를 사용한다.

```kotlin
fixtureMonkey.giveMeBuilder<Product>()
    .acceptIf(
        { it.productType == ProductType.CLOTHING },
        { builder -> builder.setExp(Product::price, 1000) }
    )
```

## 후기

테스트 데이터를 위한 Fixture를 어떻게 관리할 지 고민을 했었다. 사실 개인적으로는 되게 편한 라이브러리면서도 다소 아쉬운 부분이 존재하는 것 같다.

아래는 내가 라이브러리를 검토해보기 전에 의문이 들었던 부분에 대한 확인 결과이다.
- 객체를 검증을 통과할 수 있는 상태로 생성하기 위해 코드가 복잡할 것 같음
  - 실제로 다소 복잡한 것이 맞는 것 같다.
  - Fixture와 실제 클래스 명세와 동기화 하는 것도 어려울 것 같다.
- 테스트가 멱등하지 않을 것 같다.
  - 실제로 그런 것 같다. 랜덤성의 경우 엣지 케이스를 잡을 수 있다는 장점이 있다. 하지만, 불완전한 테스트를 짜고 그 것이 나중에 발견되는 것이 큰 이점이 있을까..의 의문이 사라지진 않는 것 같다.
  - 그냥 테스트 커버리지를 100%로 맞추면 되는 것이 아닐까 싶기도 하다.
- 가독성이 좋지 않을 것 같다.
  - 이 부분은 private method를 잘 활용한다면 가독성이 괜찮게 관리될 수는 있을 것 같다. 

그리고 FixtureMonkey는 Interface, Generic, Self reference class, Seald class 등 대부분의 상황에서 Fixture 생성을 지원한다. 그런 부분들은 충분히 장점인 것 같다.

코틀린에서는 프로퍼티를 설정할 때 변수명(String)을 사용하지 않을 수 있어서 리팩토링 내성 부분도 개선되는 것 같다.

## 참고

- https://naver.github.io/fixture-monkey/v1-0-0-kor/docs/introduction/overview
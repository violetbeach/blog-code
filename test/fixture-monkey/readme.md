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

## 유효성

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

## 커스텀 객체

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


Reactive Programming을 사용할 때 Project Reactor를 주로 사용한다.

Reactor는 비동기로 동작하기 때문에 일반적인 테스트 방식으로 검증하기 어렵다. 그래서 `Reactor-test` 라이브러리를 제공한다.

## Dependency

우선 예시 동작을 위해서 아래와 같은 Dependency를 추가해야한다.

```groovy
testImplementation 'io.projectreactor:reactor-test:3.6.5'
```

## Reactor Test가 없다면

#### 강제 동기화

아래 테스트를 보자.

```kotlin
@Test
void test() {
    // given
    var expected = IntStream.range(0, 10).boxed()
        .collect(Collectors.toList());

    // when
    Flux<Integer> result = Flux.range(0, 10)
        .delayElements(Duration.ofSeconds(1));

    // then
    assertIterableEquals(expected, result.collectList().block());
}
```

테스트의 통과를 위해 `result.collectList().block()`와 같은 비동기 코드의 강제 동기화가 필요하다.

즉, 비동기 코드의 이점을 못살리는 것이다.

#### 다양한 테스트의 부족

```kotlin
@Test
void test() {
    // when
    Flux<Integer> result = Flux.create(sink -> {
        for (int i = 0; i < 10; i++) {
            sink.next(i);
            if (i == 5) {
                sink.error(new RuntimeException("error"));
            }
        }
        sink.complete();
    });

    // then
    result.collectList().blocking() // 복잡한 검증을 어떻게 ..
}
```

해당 테스트의 then에서 1, 2, 3, 4가 잘 전달이 되었는 지, 기대했던 예외가 터졌는 지 등을 복합적으로 테스트하기 어렵다.

## Reactor Test

위와 같은 문제를 해결하기 위해 나온 Reactor Test에서 제공하는 기능에 대해 알아보자.

#### StepVerifier

StepVerifier를 사용하면 Publisher가 제공하는 다양한 이벤트를 차례로 검증할 수 있다.

StepVerifier는 FirstStep, Step, LastStep으로 구성된다. 아래 코드를 보자.
- FirstStep은 없거나 1개 구성할 수 있다.
- Step은 없거나 N개를 구성할 수 있다.
- LastStep은 최종 결과를 검증하고 반드시 1개를 구성한다.

아래 코드를 보자.

```java
@Test
void test() {
    Flux<Integer> result = Flux.create(sink -> {
        for (int i = 0; i < 10; i++) {
            sink.next(i);
        }
        sink.complete();
    });
    
    StepVerifier.create(result)
            .expectSubscription()
            .expectNext(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .expectComplete()
            .verify();
}
```

StepVerifier를 create하면 테스트를 위한 환경이 준비된 것이다. 아직 실행한 상황이 아니다.

`verify()`를 호출하면 실제로 Flux가 실행되면서 `expectSubscription()`, `expectNext()`, `expectComplete()`를 통해서 이벤트를 확인한다.

#### First Step

FirstStep은 StepVerifier의 정적 메서드인 `create()`로 생성되는 인터페이스이다.

```java
static <T> FirstStep<T> create(Publisher<? extends T> publisher) {
	return create(publisher, Long.MAX_VALUE);
}
```

FirstStep은 아래의 메서드를 가진다.

```java
interface FirstSte<T> extends Step<T> {
    Step<T> expectNoFusionSupport();
    Step<T> expectSubscription();
    Step<T> expectSubscriptionMatches(Predicate<? super Subscription> predicate);
}
```

FirstStep은 Step을 상속하므로 동작을 생략하고 바로 Step으로 넘어갈 수도 있다.




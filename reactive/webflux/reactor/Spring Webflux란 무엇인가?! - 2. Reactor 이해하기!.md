아래 포스팅에서 ReactiveProgramming에 대해 설명했고, Project Reactor에 대해서도 간단하게 설명했었다.
- https://jaehoney.tistory.com/359

해당 포스팅에서 Project Reactor에 대해서 더 자세한 내용을 다룬다.

## Subscribe

Publisher가 item을 전달하면 그 아이템을 받아서 처리하는 것을 subscribe라고 한다.

즉, item이 있어도 subscribe되지 않으면 아무 일도 일어나지 않는다.

```java
Flux.fromIterable(List.of(1, 2, 3, 4, 5))
        .doOnNext(value -> {
            log.info("value: " + value);
        })
        // .subscribe()를 하지 않으면 아무 일도 일어나지 않음
```

아래는 Mono와 Flux가 사용하는 subscribe의 명세이다. 오버로딩된 3개 메서드가 있음을 볼 수 있다.

```java
public Disposable subscribe()

public final Disposable subscribe
    @Nullable Consumer<? super T> consumer,
    @Nullable Consumer<? super Throwable> errorConsumer,
    @Nullable Runnable completeConsumer,
    @Nullable Context initialContext)

private final void subscribe(Subscriber<? super T> actual)
```

- Consumer를 넘기지 않는 subscribe
  - 별도로 Consume을 하지 않고 최대한으로 요청
- 함수형 인터페이스 기반의 subscribe
  - Disposable을 반환하고 반환된 객체를 통해 언제든지 연결 종료할 수 있다.
- Subscriber 기반의 subscribe
  - Subscriber는 subscription을 받기 때문에 request와 cancel으로 backpressure를 조절할 수 있다.

아래 코드를 보자.

```java
Flux.fromIterable(List.of(1, 2, 3, 4, 5))
        .doOnNext(value -> {
            log.info("value: " + value);
        })
        .subscribe()
```

해당 코드는 별도의 Consumer를 넘기지 않는 subscribe이다.
- 결과를 이용하기 보다는 아이템을 만드는 것이 중요한 경우 사용한다.
- 결과를 확인하기 위해 `doOnNext()`를 활용한다.

아래는 함수형 인터페이스 기반의 subscribe이다.

```java
Flux.fromIterable(List.of(1, 2, 3, 4, 5))
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                log.info("value: " + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                log.error("error: " + throwable);
            }
        }, new Runnable() {
            @Override
            public void run() {
                log.info("complete");
            }
        }, Context.empty());
```

subscribe에는 총 4가지 인자를 넘길 수 있다.
- consumer: 값은 하나씩 인자로 받아서 처리
- errorConsumer: 에러가 발생했을 때 인자로 받아서 처리
- completeConsumer: 완료 후에 인자 없이 Runnable 실행
- initialContext: upstream에 전달할 context

두 방법은 모두 backpressure를 사용할 수 없다. 이 경우 Subscriber 기반의 subscribe를 사용할 수 있다.

```java
Flux.fromIterable(List.of(1, 2, 3, 4, 5))
        .subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }
    
            @Override
            public void onNext(Integer integer) {
                log.info("value: " + integer);
            }
    
            @Override
            public void onError(Throwable t) {
                log.error("error: " + t);
            }
    
            @Override
            public void onComplete() {
                log.info("complete");
            }
    });
```

Subscriber 기반의 subscribe의 경우 `onSubscribe()`로 backpressure를 조절할 수 있다.

외부에서 Subscriber를 전달하는 경우 `request()`를 직접 호출하거나 `cancel()`을 처리하는 등의 제어도 가능하다.

#### Unbounded Request

Unbounded Request란 `request(Long.MAX_VALUE)`처럼 backpressure를 비활성화하고 가능한 빠르게 아이템을 전달해달라는 요청이다.

Unbounded Request는 아래 상황에 발생한다.
- 아무 것도 넘기지 않는 subscribe()
- 람다 기반의 subscribe()
- block(), blockFirst(), blockLast() 등의 blocking 연산
- toIterable(), toStream() 등의 toCollect 연산자

#### buffer

backpressure 조절과 함께 활용할 수 있는 게 buffer 연산이다.

buffer(N)을 호출 시 N개 만큼 item을 모아서 List로 전달한다.

```java
var subscriber = new BaseSubscriber<List<Integer>>() {
    private Integer count = 0;

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        request(2);
    }

    @Override
    protected void hookOnNext(List<Integer> value) {
        if (++count == 2) cancel();
    }

    @Override
    protected void hookOnComplete() {
        log.info("complete");
    }
};

Flux.fromStream(IntStream.range(0, 10).boxed())
        .buffer(3)
        .subscribe(subscriber);
```

해당 코드에서는 buffer(3)을 호출 후 request(2)를 하기 때문에 3개가 담긴 List 2개가 Subscriber에게 전달된다.

#### take

take 연산은 subscriber 외부에서 최대 개수를 제한할 수 있다.

take(n)는 정확히 n개만큼 요청 후 complete 이벤트를 전달한다.

```java
var subscriber = new BaseSubscriber<Integer>() {
    @Override
    protected void hookOnNext(Integer value) {
        log.info("value: " + value);
    }

    @Override
    protected void hookOnComplete() {
        log.info("complete");
    }
};

Flux.fromStream(IntStream.range(0, 10).boxed())
        .take(5)
        .subscribe(subscriber);
```

위 코드의 경우 5개의 값만 전달되고 완료로 처리된다.

## Sequence
아래 포스팅에서 ReactiveProgramming에 대해 설명했고, Reactive Stream과 Project Reactor에 대해서도 간단하게 설명했었다.
- https://jaehoney.tistory.com/359

개념 자체가 생소하다면 해당 포스팅을 참고하시길 추천한다.

이번 포스팅에서 Project Reactor의 사용 방법에 대해 다룬다.

## Subscribe

Mono와 Flux가 구현하는 CorePublisher 인터페이스이다.

```java
public interface CorePublisher<T> extends Publisher<T> { 
    void subscribe(CoreSubscriber<? super T> subscriber);
}
```

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

Sequence란 Reactor에서 통지할 데이터를 정의한 것을 말한다. 데이터의 흐름 Stream과 유사하다고 생각하면 된다.


#### just

`Mono.just()`, `Flux.just()`를 사용하면 주어진 객체를 subscriber에게 전달할 수 있다.

```java
Mono.just(1)
        .subscribe(value -> {
            log.info("value: " + value);
        });

Flux.just(1, 2, 3, 4, 5)
        .subscribe(value -> {
            log.info("value: " + value);
        });
```

#### error

`Mono.error()`, `Flux.error()`를 통해 subscriber에게 onError 이벤트만 전달한다.

```java
Mono.error(new RuntimeException("mono error"))
         .subscribe(value -> {
             log.info("value: " + value);
         }, error -> {
             log.error("error: " + error);
         });

 Flux.error(new RuntimeException("flux error"))
         .subscribe(value -> {
             log.info("value: " + value);
         }, error -> {
             log.error("error: " + error);
         });
```

#### empty

`Mono.empty()`, `Flux.empty()`를 통해 시퀀스를 생성할 수 있다.

```java
Mono.empty()
        .subscribe(value -> {
            log.info("value: " + value);
        }, null, () -> {
            log.info("complete");
        });
Flux.empty()
        .subscribe(value -> {
            log.info("value: " + value);
        }, null, () -> {
            log.info("complete");
        });
```

이 경우에는 subscriber에게 onComplete 이벤트만 전달한다.

가운데 null은 에러 컨슈머를 null로 사용한 것이다.

#### from

실무를 하다보면 더 복잡한 경우도 생길 수 있다.

예를 들면 Callable, Runnable 등을 실행한 결과를 Mono, Flux한테 넘기는 경우 등에서 fromX를 사용할 수 있다.

#### Mono

- fromCallable: Callable 함수형 인터페이스를 실행하고 반환 값을 onNext로 전달
- fromFuture: Future를 받아서 done 상태가 되면 반환 값을 onNext로 전달
- fromSupplier: Supplier 함수형 인터페이스를 실행하고 반환 값을 onNext로 전달
- fromRunnable: Runnable 함수형 인터페이스를 실행하고 onComplete 전달

```java
Mono.fromCallable(() -> {
    return 1;
}).subscribe(value -> {
    log.info("value fromCallable: " + value);
});

Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
    return 1;
})).subscribe(value -> {
    log.info("value fromFuture: " + value);
});

Mono.fromSupplier(() -> {
    return 1;
}).subscribe(value -> {
    log.info("value fromSupplier: " + value);
});

Mono.fromRunnable(() -> {
    /* do nothing */
}).subscribe(null, null, () -> {
    log.info("complete fromRunnable");
});
```

#### Flux

Flux의 from은 여러 개의 값을 받아서 onNext로 전달한다. 

- fromIterable: Iterable를 받아서 각각의 item을 onNext로 전달
- fromStream: Stream을 받아서 각각의 item을 onNext로 전달
- fromArray: Array를 받아서 각각의 item을 onNext로 전달
- range(start, n): start부터 시작해서 1개씩 커진 값을 n개만큼 onNext로 전달

```java
Flux.fromIterable(
        List.of(1, 2, 3, 4, 5)
).subscribe(value -> {
    log.info("value: " + value);
});

Flux.fromStream(
        IntStream.range(1, 6).boxed()
).subscribe(value -> {
    log.info("value: " + value);
});

Flux.fromArray(
        new Integer[]{1, 2, 3, 4, 5}
).subscribe(value -> {
    log.info("value: " + value);
});

Flux.range(
        1, 5
).subscribe(value -> {
    log.info("value: " + value);
});
```

#### generate

아래의 Flux.fromX를 사용하면 간단한 sequence를 만들 수 있다.

더 복잡한 경우도 생길 수 있다. 조건문이 들어간다거나, 콜백을 실행한 후 값을 sequence에 넣어줘야 한다거나 하는 경우에서는 generate를 사용할 수 있다.

```java
public static <T, S> Flux<T> generate(
        Callable<S> stateSupplier,
        BiFunction<S, SynchronousSink<T>, S> generator)
```

generate는 아래의 작업을 수행한다.
- 동기적으로 Flux를 생성
- stateSupplier: 초기 값을 제공하는 Callable
- generator:
  - 첫 번째 인자로 state를 제공, 변경된 state를 반환
  - 두 번째 인자로 SynchronousSink를 제공. 명시적으로 next, error, Complete 호출 가능
  - 한 번의 generator에서 최대 한 번만 next 호출 가능

설명만 봐서는 잘 이해가 되지 않아서 예제 코드를 보자.

```java
Flux.generate(
        () -> 0,
        (state, sink) -> {
            sink.next(state);
            if (state == 9) {
                sink.complete();
            }
            return state + 1;
        }
).subscribe(value -> {
    log.info("value: " + value);
}, error -> {
    log.error("error: " + error);
}, () -> {
    log.info("complete");
});
```

해당 코드의 동작은 아래와 같다.
- 초기 값(state)를 0으로 세팅했다.
- generator에서 현재 state를 next로 반환한다.
- state가 9라면 complete 이벤트를 전달한다.
- state + 1을 반환한다.

#### create

한 번의 generate에서 next를 두 번이상 호출하면 에러가 발생한다.

만약 next를 많이 호출해야 하거나 더 복잡한 케이스를 커버하려면 create를 활용할 수 있다.

```java
public static <T> Flux<T> create(
        Consumer<? super FluxSink<T>> emitter)
```

create는 아래 작업을 수행한다.
- 비동기적으로 Flux를 생성
- FluxSink를 노출
  - 명시적으로 next, error, complete 호출 가능
  - emitter 1번에서 next를 여러 번 호출 가능
  - 여러 thread에서 동시에 호출 가능

아래 코드르 롭자.

```java
Flux.create(sink -> {
    var task1 = CompletableFuture.runAsync(() -> {
        for (int i = 0; i < 5; i++) {
            sink.next(i);
        }
    });

    var task2 = CompletableFuture.runAsync(() -> {
        for (int i = 5; i < 10; i++) {
            sink.next(i);
        }
    });

    CompletableFuture.allOf(task1, task2)
            .thenRun(sink::complete);
}).subscribe(value -> {
    log.info("value: " + value);
}, error -> {
    log.error("error: " + error);
}, () -> {
    log.info("complete");
});
```

아래 코드는 아래 역할을 수행한다.
- 2개의 쓰레드에서 sink.next를 수행
- CompletableFuture의 allOf를 활용하여 두 개의 작업이 끝난 후 complete 이벤트 전달

결과적으로 0~4와 5~9가 전달되고 각각 순서까지만 보장하고, 0~4와 5~9 사이에서는 순서가 보장되지 않는다.

```
25:38 [ForkJoinPool.commonPool-worker-19] - value: 0
25:38 [ForkJoinPool.commonPool-worker-19] - value: 5
25:38 [ForkJoinPool.commonPool-worker-19] - value: 6
25:38 [ForkJoinPool.commonPool-worker-19] - value: 7
25:38 [ForkJoinPool.commonPool-worker-19] - value: 8
25:38 [ForkJoinPool.commonPool-worker-19] - value: 9
25:38 [ForkJoinPool.commonPool-worker-19] - value: 1
25:38 [ForkJoinPool.commonPool-worker-19] - value: 2
25:38 [ForkJoinPool.commonPool-worker-19] - value: 3
25:38 [ForkJoinPool.commonPool-worker-19] - value: 4
25:38 [ForkJoinPool.commonPool-worker-19] - complete
```

#### handle

generate(), create()까지 사용하면 대부분의 경우 sequence를 만들 수 있다. 

여기서 추가로 값을 Intercept해서 특정 값을 필터링하거나 가공하는 등의 처리를 할 때 handle을 사용할 수 있다.

```java
public final <R> Flux<R> handle(
        BiConsumer<? super T, SynchronousSink<R>> handler)
```

handle은 아래 동작을 수행한다.
- 독립적으로 sequence를 생성할 수 없고 존재하는 source에 연결
- handler
  - 첫 번째 인자로 source의 item을 제공
  - 두 번째 인자로 SynchronousSink를 제공
    - sink의 next를 이용해서 현재 주어진 item을 전달할 지 말 지 결정
- 일종의 interceptor로 source의 item을 필터하거나 변경할 수 있다.

아래 코드를 보자.

```java
Flux.fromStream(IntStream.range(0, 10).boxed())
        .handle((value, sink) -> {
            if (value % 2 == 0) {
                sink.next(value);
            }
        }).subscribe(value -> {
            log.info("value: " + value);
        }, error -> {
            log.error("error: " + error);
        }, () -> {
            log.info("complete");
        });
```

해당 코드에서는 짝수인 경우에만 sink의 next를 호출한다.

```
34:58 [main] - value: 0
34:58 [main] - value: 2
34:58 [main] - value: 4
34:58 [main] - value: 6
34:58 [main] - value: 8
34:58 [main] - complete
```

handle의 sink를 사용해서 complete나 error를 더 일찍 전달하는 방식으로 사용할 수 있다는 점도 참고하자.

## 참고
- https://fastcampus.co.kr/courses/216172
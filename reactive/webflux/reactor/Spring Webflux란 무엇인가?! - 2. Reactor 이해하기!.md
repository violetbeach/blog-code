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

#### delayElements

많이 사용하는 연산자 중에 delayElements라는 연산이 있다.

- 최소 delay 만큼 간격을 두고 onNext 이벤트 발행
- onNext 이벤트가 발행된 후 더 늦게 다음 onNext 이벤트가 전달되면 즉시 전파

즉, 이를 사용하면 처리량을 제한할 수 있다.

```java
Flux.create(
        sink -> {
            for (int i = 1; i <= 5; i++) {
                sleep(1000);
                sink.next(i);
            }
            sink.complete();
        })
        .delayElements(Duration.ofMillis(5000))
        .doOnNext(value -> log.info("doOnNext: " + value))
        .subscribe();
```

해당 코드는 5초마다 onNext로 각 데이터가 전달된다.

#### concat

concat을 사용하면 Publisher 들을 결합할 수 있다.

내부 동작은 아래와 같다.
- 이전의 Publisher가 onComplete 이벤트를 전달되면 다음 Publisher를 subscribe

코드를 보자

```java
var flux1 = Flux.range(1, 3)
        .doOnSubscribe(value -> log.info("doOnSubscribe1"))
        .delayElements(Duration.ofMillis(100));
var flux2 = Flux.range(10, 3)
        .doOnSubscribe(value -> log.info("doOnSubscribe2"))
        .delayElements(Duration.ofMillis(100));

Flux.concat(flux1, flux2)
        .doOnNext(value -> log.info("doOnNext: " + value))
        .subscribe();
```

아래는 코드를 실행한 결과이다.
```
00:44 [main] - doOnSubscribe1
00:44 [parallel-1] - doOnNext: 1
00:44 [parallel-2] - doOnNext: 2
00:45 [parallel-3] - doOnNext: 3
00:45 [parallel-3] - doOnSubscribe2
00:45 [parallel-4] - doOnNext: 10
00:45 [parallel-5] - doOnNext: 11
00:45 [parallel-6] - doOnNext: 12
```

Publisher의 내부 순서도 보장하고, 인자로 전달된 각 Publisher 간 순서도 보장된다.

그래서 처리량은 다소 떨어지게 된다.

#### merge

merge도 Publisher를 결합하는 연산이다.

단, concat과 다르게 모든 Publisher를 바로 subscribe하고 각각의 Publisher의 onNext 이벤트가 동시에 도달된다.

```java
var flux1 = Flux.range(1, 3)
        .doOnSubscribe(value -> log.info("doOnSubscribe1"))
        .delayElements(Duration.ofMillis(100));
var flux2 = Flux.range(10, 3)
        .doOnSubscribe(value -> log.info("doOnSubscribe2"))
        .delayElements(Duration.ofMillis(100));

Flux.merge(flux1, flux2)
        .doOnNext(value -> log.info("doOnNext: " + value))
        .subscribe();
```

아래는 해당 코드 실행 결과이다.

```
07:45 [main] - doOnSubscribe1
07:45 [main] - doOnSubscribe2
07:45 [parallel-1] - doOnNext: 1
07:45 [parallel-1] - doOnNext: 10
07:45 [parallel-3] - doOnNext: 11
07:45 [parallel-4] - doOnNext: 2
07:45 [parallel-5] - doOnNext: 12
07:45 [parallel-6] - doOnNext: 3
07:46 [main] - end main
```

Publisher의 내부 순서는 보장하지만, 인자로 전달된 Publisher 간 순서를 보장하지 않는다.

참고로 mergeSequential이라는 순서를 보장하는 연산자도 지원한다.
- 동시에 실행된 결과를 내부적으로 재정렬하는 방식

#### 다양한 연산자

그 밖에도 다양한 연산자가 지원된다. 대부분 Stream에서도 사용하기 때문에 간략하게만 소개한다.
- 

다음은 Thread와 Scheduler에 대해 알아보자.

## Thread

Reactor에서의 subscribe랑 sequence 개념과 사용 방법에 대해 익혔다.

subscribe를 하면 어떤 쓰레드에서 실행되는 걸까?

**기본적으로**는 Publisher랑 subscribe가 같은 쓰레드에서 실행된다. 즉, 기본적으로는 동기적으로 동작한다고 볼 수 있다.

- 별도의 설정이 없다면 subscribe를 호출한 caller의 쓰레드에서 실행된다.

아래 코드를 실행해보자.

```java
public static void main(String[] args) {
    var executor = Executors.newSingleThreadExecutor();
    try {
        executor.submit(() -> {
            log.info("start!");
            Flux.create(sink -> {
                for (int i = 1; i <= 5; i++) {
                    log.info("next: {}", i);
                    sink.next(i);
                }
            }).subscribe(value -> {
                log.info("value: " + value);
            });
        });
    } finally {
        executor.shutdown();
    }
}
```

결과 아래와 같이 동일한 쓰레드를 사용함을 알 수 있다.

```
26:36 [pool-2-thread-1] - start!
26:36 [pool-2-thread-1] - next: 1
26:36 [pool-2-thread-1] - value: 1
26:36 [pool-2-thread-1] - next: 2
26:36 [pool-2-thread-1] - value: 2
26:36 [pool-2-thread-1] - next: 3
26:36 [pool-2-thread-1] - value: 3
26:36 [pool-2-thread-1] - next: 4
26:36 [pool-2-thread-1] - value: 4
26:36 [pool-2-thread-1] - next: 5
26:36 [pool-2-thread-1] - value: 5
```

## Shceduler

Scheduler로 Publish 혹은 Subscribe에 task를 실행하는 쓰레드 풀을 설정할 수 있고, Task를 언제 수행할 지 설정할 수 있다.
- ImmediateScheduler
  - subscribe를 호출한 caller 쓰레드에서 즉시 실행한다.
  - 별도 Scheduler를 넘기지 않는다면 기본으로 사용된다.
- SingleScheduler
  - 캐싱된 1개 크기의 쓰레드 풀을 제공
  - 모든 publish, subsscribe가 하나의 쓰레드에서 실행
- ParallelScheduler
  - 캐싱된 n개 크기의 쓰레드 풀을 제공
  - 기본적으로 CPU 코어 수만큼의 크기를 갖는다.
- BoundedElasticScheduler
  - 캐싱된 고정되지 않은 크기의 쓰레드 풀을 제공
  - 재사용할 수 있는 쓰레드가 있다면 사용하고, 없으면 새로 생성
  - 특정 시간(default  = 60s) 사용하지 않으면 제거
  - 생성 가능한 최대 쓰레드 수는 CPU 코어 수 x 10
  - I/O Blocking 작업을 수행할 때 적합
- ...

Scheduler는 subscribeOn()으로 설정할 수 있다.

```java
Flux.create(sink -> {
    for (int i = 1; i <= 5; i++) {
        log.info("next: {}", i);
        sink.next(i);
    }
}).subscribeOn(
        Schedulers.single()
).subscribe(value -> {
    log.info("value: " + value);
});
```

#### Schuedler.newXX

`Schedulers.single()`를 사용한다면 해당 스케줄러를 사용하는 작업들이 스레드 풀을 공유한다.

매번 새로운 쓰레드 풀을 할당하거나 중요한 작업들을 위해 별도의 쓰레드 풀을 할당해야 한다면 Scheduler.newXX를 사용할 수 있다.

- newSingle()
- newParallel()
- newBoundedElastic()

아래 코드는 Schedulers.newSingle()을 사용해서 쓰레드 풀을 다른 작업과 공유하지 않는다.

```java
public class SingleService {
    Scheduler newSingle = Schedulers.newSingle("single");

    void singleSchedulerTest(int idx) {
        Flux.create(sink -> {
            log.info("next: {}", idx);
            sink.next(idx);
        }).subscribeOn(
                newSingle
        ).subscribe(value -> {
            log.info("value: " + value);
        });
    }
}
```

#### fromExecutorService

ExecutorService 사용에 익숙하다면 아래와 같이 Scheduler 인스턴스를 생성할 수도 있다.
- Schedulers.fromExecutorService(executorService)

#### publishOn

subscribeOn으로 스케줄러를 조정할 수 있었는데, publishOn을 사용해서 이후에 추가되는 연산자들의 스케줄러를 설정할 수 있다.
- publishOn은 subscribeOn과 다르게 위치가 중요하다.
- 그 이후 다른 publishOn이 적용되면 추가된 Scheduler로 실행 쓰레드 변경
- 쓰레드 풀에서 1개의 쓰레드만 지속적으로 사용

아래 예시를 보자.

```java
Flux.create(sink -> {
    for (var i = 0; i < 3; i++) {
        log.info("next: {}", i);
        sink.next(i);
    }
}).publishOn(
        Schedulers.single()
).doOnNext(item -> {
    log.info("doOnNext: {}", item);
}).subscribe(value -> {
    log.info("value: " + value);
});
```

결과는 아래와 같다.

```
19:20 [main] - next: 0
19:20 [main] - next: 1
19:20 [main] - next: 2
19:20 [single-1] - doOnNext: 0
19:20 [single-1] - value: 0
19:20 [single-1] - doOnNext: 1
19:20 [single-1] - value: 1
19:20 [single-1] - doOnNext: 2
19:20 [single-1] - value: 2
```

아래는 publishOn과 subscribeOn을 같이 사용한 예시이다.

```java
Flux.create(sink -> {
    for (var i = 0; i < 3; i++) {
        log.info("next: {}", i);
        sink.next(i);
    }
}).publishOn(
        Schedulers.single()
).doOnNext(item -> {
    log.info("doOnNext: {}", item);
}).publishOn(
        Schedulers.boundedElastic()
).doOnNext(item -> {
    log.info("doOnNext2: {}", item);
}).subscribeOn(Schedulers.parallel()
).subscribe(value -> {
    log.info("value: " + value);
});
```

결과는 아래와 같다.

```java
28:46 [parallel-1] - next: 0
28:46 [parallel-1] - next: 1
28:46 [parallel-1] - next: 2
28:46 [single-1] - doOnNext: 0
28:46 [single-1] - doOnNext: 1
28:46 [single-1] - doOnNext: 2
28:46 [boundedElastic-1] - doOnNext2: 0
28:46 [boundedElastic-1] - value: 0
28:46 [boundedElastic-1] - doOnNext2: 1
28:46 [boundedElastic-1] - value: 1
28:46 [boundedElastic-1] - doOnNext2: 2
28:46 [boundedElastic-1] - value: 2
```

동작을 설명하면 아래와 같다.
- subscribeOn이 소스에 영향을 주기 때문에 소스가 parallel도 동작을 한다.
- 이후 동작부터는 publishOn으로 인해 single로 동작을 한다.
- 이후 동작부터는 새로운 publishOn으로 인해 boundedElastic으로 동작한다. 

다음은 Reactor에서 에러 핸들링에 대해 알아보자.

## 에러 핸들링

Reactive streams에서 onError 이벤트가 발생하면 onNext, onComplete 이벤트를 생산하지 않고 onError 이벤트를 아래로 쭉 전파하고 종료한다.

onError 이벤트는 아래의 방식으로 처리할 수 있다.
- 고정된 값을 반환
- publisher를 반환
- onComplete 이벤트로 변경
- 다른 에러로 변환

#### 에러 핸들링이 없을 경우?

에러 핸들링이 없으면 내부적으로 onErrorDropped를 호출하게 된다.

```java
public static void onErrorDropped(Throwable e, Context context) {
    Consumer<? super Throwable> hook = context.getOrDefault(Hooks.KEY_ON_ERROR_DROPPED,null);
    if (hook == null) {
        hook = Hooks.onErrorDroppedHook;
    }
    if (hook == null) {
        log.error("Operator called default onErrorDropped", e);
        return;
    }
    hook.accept(e);
}
```

내부적으로 log.error()를 사용해서 로그를 출력한다.

#### ErrorConsumer

에러 핸들링의 가장 쉬운 방법 중 하나가 subscribe의 두번째 인자인 errorConsumer를 활용하는 방법이다.

```java
Flux.error(new RuntimeException("error"))
        .subscribe(value -> {
            log.info("value: " + value);
        }, error -> {
            log.info("error: " + error);
        });
```

#### onErrorReturn

ErrorConsumer는 특정 Action만 수행하지 결과를 반환하기 어렵다.

이때 onErrorReturn을 사용할 수 있다.

```java
Flux.error(new RuntimeException("error"))
        .onErrorReturn(0)
        .subscribe(value -> {
            log.info("value: " + value);
        });
```

onErrorReturn을 사용하면 고정된 값을 반환할 수 있다. 단, onErrorReturn에는 함수를 전달할 수 없다.
- 인자로 함수의 결과를 전달한다면 Subscribe도 되기 전에 동작할 것이다.

#### onErrorResume

onErrorReturn은 함수를 전달 받을 수 없었다.

onErrorResume은 함수형 인터페이스를 전달 받아서 에러가 발생한 경우 함수형 인터페이스의 결과를 다음 subscribe에 전달할 수 있다. 

```java
Flux.error(new RuntimeException("error"))
        .onErrorResume(throwable -> Flux.just(0, -1, -2))
        .subscribe(value -> {
            log.info("value: " + value);
        });
```

onErrorResume을 사용하면 실제로 에러가 발생한 경우에만 함수형 인터페이스를 실행하게 된다.

#### onErrorComplete

onErrorComplete는 onError 이벤트를 onComplete 이벤트로 변경한다.

```java
Flux.create(sink -> {
    sink.next(1);
    sink.next(2);
    sink.error(new RuntimeException("error"));
}).onErrorComplete()
        .subscribe(
                value -> log.info("value: " + value),
                null,
                () -> log.info("complete"));
```

위 코드는 2번은 정상적으로 Consumer가 동작하고, 세 번째에 ErrorConsumer가 동작하지 않고, CompleteConsumer가 동작하게 된다.

#### onErrorMap

IOException을 커스텀 비즈니스 익셉션으로 핸들링 하는 경우 아래와 같이 onErrorMap을 사용할 수 있다.

```java
Flux.error(new IOException("fail to read file"))
        .onErrorMap(e -> new CustomBusinessException("custom"))
        .subscribe(value -> log.info("value: " + value),
                e -> log.info("error: " + e));
```

onError는 예외를 다른 예외로 변환한다.

#### doOnError

에러를 변환할 필요가 없고, ErrorConsumer까지 전달되기 전에 처리가 필요하다면 doOnError를 활용할 수도 있다.

```java
Flux.error(new RuntimeException("error"))
        .doOnError(error -> log.info("doOnError: " + error))
        .subscribe(value -> log.info("value: " + value), 
                error -> log.info("error: " + error));
```

## 참고
- https://fastcampus.co.kr/courses/216172
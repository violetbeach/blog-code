## runBlocking은 제거해야 할까?

특정 프로젝트에 투입되어서 코틀린과 코루틴을 처음 접하는 경우가 많을 것이다.

대부분 Controller 메서드의 시작이 `runBlocking()`이 되어있고, 사실상 코루틴은 `runBlocking()`, `runCatching()` 밖에 존재하지 않는 경우가 있다.  

내가 투입된 프로젝트의 코드도 동일한 상황이었고 그랬고, '코루틴을 사용할 때의 이점을 하나도 못누리고 있는 것은 아닐까..?' 하는 생각을 가지게 되었다.

`runBlocking`은 왜 문제이며, 어떻게 개선할 수 있는 지 알아보자.

## runBlocking

아래는 `runBlocking`에 대한 docs의 일부이다.

> Runs a new coroutine and blocks the current thread interruptibly until its completion.

해당 docs를 읽어보면 `runBlocking`은 실행한 Thread를 작업이 완료할 때까지 Blocking 한다는 것을 알 수 있다. 즉, 기존의 동기코드와 동일하게 동작한다는 것이다.

## Problem

일반적으로 코루틴을 사용하는 이유는 병렬 프로그래밍을 쉽고 편리하게 하기 위해서이다. `runBlocking` 만을 사용한다면 동기 코드를 더 복잡하게만 만드는 행위일 수 있다.

아래는 공식 문서의 설명이다.

> The name of `runBlocking` means that the thread that runs it (in this case — the main thread) gets **blocked** for the duration of the call, until all the coroutines inside `runBlocking { ... }` complete their execution. You will often see `runBlocking` used like that at the very top-level of the application and quite rarely inside the real code, as threads are expensive resources and blocking them is inefficient and is often not desired.

가장 중요한 부분은 아래와 같다.

> 쓰레드는 값비싼 자원이고, 이를 차단하는 것은 일반적으로 비효율적이다. 그렇기 때문에  최상위 수준에서 사용되는 `runBlocking`은 실제로 거의 사용되지 않는다.

즉, `runBlocking`을 Controller method처럼 상위 레벨에서 사용하는 것은 코루틴의 이점을 활용하지 못한다는 것을 의미한다. 게다가 오히려 잘못된 사용을 야기할 수 있다.

예시를 보자. 아래의 OrderController가 있다.

```kotlin
@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
){

    @GetMapping
    fun order() = runBlocking {
        orderService.order()
    }
}
```

해당 OrderController는 아래의 OrderService의 order()를 호출한다.

```kotlin
@Service
class OrderService {

    suspend fun order() = runBlocking(Dispatchers.Order) {
        delay(100)
        println("주문이 완료되었습니다.")
    }
}
```

해당 코드를 보면 Main Thread는 어차피 Dispatchers.Order가 관리하는 쓰레드의 작업이 종료될 때까지 다른 작업을 수행할 수 없고 기다려야 한다.

즉, Thread가 1개만 필요한 상황에서 불필요하게 Thread를 2개 사용하는 상황이 된 것이다.

## Spring MVC (+Webflux)

Spring Webflux가 Controller의 suspend 메서드를 지원하는 것은 익히 알고 있다. Spring Webflux를 사용한다면 Controller 메서드에서는 `runBlocking` 메서드를 호출할 필요가 없다.

```kotlin
@RestController
@RequestMapping("/hello")
class HelloController {
    private val log = logger<HelloController>()

    @GetMapping
    suspend fun hello() {
        log.info("context: {}", coroutineContext)
        log.info("thread: {}", Thread.currentThread().name)
    }
}
```

해당 컨트롤러에 요청을 보내보면 아래 로그가 찍힌다.

```kotlin
34:31 [reactor-http-nio-2] - context:
    [Context1{reactor.onDiscard.local= reactor.core.publisher.Operators$$Lambda/0x0000000123657b60@7bbfcea9}, 
    MonoCoroutine{Active}@35a52e8a, Dispatchers.Unconfined]
34:31 [reactor-http-nio-2] - thread: reactor-http-nio-2
```

실제로 `spring-web` 라이브러리를 보면 suspend 함수에 대해서 아래와 같이 `invoke`를 통해 처리하고 있다.

![img.png](img.png)

`invokeSuspendingFunction()`는 내부적으로 `Mono`로 감싸서 함수를 처리하게 된다.

![img_1.png](img_1.png)

즉, Spring Webflux에서 쓰레드를 관리해주는 역할을 책임지는 것이다.

#### Spring MVC

그렇다면 Spring MVC에서는 어떻게 될까..? 요청을 보내보면 아래의 에러가 발생한다!

```
java.lang.ClassNotFoundException: org.reactivestreams.Publisher
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641) ~[na:na]
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188) ~[na:na]
```

즉, Reactive Stream, Reactor 등이 필요한 비동기 환경으로의 변경 없이 컨트롤러 메서드에서 `suspend`를 사용할 수 없다.

생각해보면 당연한 결과다. Spring MVC는 thread-per-request 모델이다.

## 생각 및 정리

위에서 언급했듯 Servlet Container를 변경하지 않는 이상에는 Controller에서 `suspend` 메서드를 사용할 수 없다.

즉, Spring WebFlux가 아닌 Spring MVC 상황에서는 비즈니스 로직에서의 `suspend` 호출을 위해 `runBlocking`은 존재할 수 밖에 없다.
즉, Spring MVC와 Coroutine은 다소 Fit 하지 않는(어울리지 않는) 느낌이 있다.

그래서 병렬 프로그래밍을 제대로 하고 싶다면 Spring Webflux 로의 전환을 추천한다. 대부분은 Spring Webflux를 고려해서 suspend 처리를 하는 것이 더 좋다.

하지만 단순히 '`runBlocking`은 사용하면 안돼'라고 생각해서 Controller에 다른 쓰레드를 할당하는 등의 옵션은 Spring MVC의 매커니즘을 손상할 수 있다. 주어진 환경이 Spring MVC 라면 꼭 Controller Method가 아니더라도 `runBlocking`이 필요하고 자연스러울 수 있다.
- 구조화된 동시성 + 비동기 처리 등을 위 `suspend` 메서드의 호출은 바람직하기 때문에 `suspend` 메서드는 필요하다.
- Spring MVC 환경에서 해당 메서드를 호출하기 위해 `runBlocking`을 사용하는 것은 자연스럽다.


---

결론은 코드 내 대부분의 `runBlocking`을 제거하기 위해서는 Spring MVC -> WebFlux로의 전환이 필요하다는 것이다.
- WebFlux로 전환이 되었다면 Controller에서 suspend 키워드를 사용하면 된다.

단, Spring MVC 환경에서는 `runBlocking`이 필요할 수 있으며 반드시 제거해야 한다고 보기는 어렵다.
- 쓰레드를 블락한다는 사실을 주의해야 한다.

## 참고

- https://kotlinlang.org/docs/coroutines-basics.html
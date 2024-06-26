## 코루틴

코루틴(Coroutine)은 Co(함께, 서로) + routine(규칙적 작업의 집합) 2개가 합쳐진 단어로 함께 동작하며 규칙이 있는 작업의 집합을 의미한다.

왜 `Koroutine`이 아니라 `Coroutine`인지 의아할 수 있는데 코루틴은 코틀린만의 것이 아니다. Python, C#, Go, Javascript 등 다양한 언어에서 지원하는 개념이다.

JS의 async, await도 코루틴의 일부이며, 코루틴은 프로그래밍 초창기부터 존재하던 개념이다.

## vs Thread

코루틴은 **경량 쓰레드**라고 부른다.

아래는 여러 개의 쓰레드로 여러 개의 작업을 실행하는 방식이다.

![img_1.png](img_1.png)

코루틴은 작업 하나하나에 Thread를 할당하는 것이 아니라 **Object**를 할당한다. 

![img_2.png](img_2.png)

쓰레드가 Object를 스위칭함으로써 Context Swiching 비용을 대폭 줄인다.


## Kotlin Coroutines

코틀린에서는 코루틴을 위한 공식 라이브러리(`kotlinx.coroutines`)를 지원한다.

Jetbrain에서는 멀티 쓰레딩 문제를 간소화된 방식으로 해결할 수 있도록 코틀린의 코루틴 라이브러리를 개발했다고 한다.

코루틴은 높은 러닝커브를 가지는 `RxJava`와 같은 비동기 라이브러리보다 낮은 러닝커브로 동기적으로 코드를 작성할 수 있게 도움을 준다.

코틀린에서는 `suspend` 키워드를 사용해서 코루틴을 제공하고 있다.

## suspend 키워드

suspend는 coroutine 혹은 다른 `suspend` 함수에서 사용한다.

![img.png](img.png)

`suspend` 키워드를 사용하면 해당 작업을 일시중단(suspend) 시키고, 그 시간 동안 다른 작업에 Thread를 할당할 수 있다.

`suspend`의 내부 구현을 이해하려면 코틀린 컴파일러와 CPS(Continuation passing style)를 알아야 한다.

### CPS(Continuation passing style)

코루틴에서 사용하는 Continuation passing style는 Direct style과 유사하다. Direct style의 특징은 아래와 같다.
- Caller가 callee를 호출하는 상황에서 Callee는 값을 계산하여 반환
- Caller는 callee가 반환한 결과를 사용
- 일반적인 동기 스타일

아래는 Continuation passing style의 특징이다.
- Caller가 callee를 호출하는 상황에서 Callee는 값을 계산하여 Continuation을 실행하고 인자로 값을 전달
- continuation은 callee 마지막에서 한 번만 실행한다.

아래 코드를 보자.

```kotlin
object CpsCalculator {
    fun calculate(initialValue: Int, continuation: (Int) -> Unit) {
        initialize(initialValue) { initial ->
            plusOne(initial) { added ->
                double(added) { multiplied ->
                    continuation(multiplied)
                }
            }
        }
    }

    private fun initialize(value: Int, continuation: (Int) -> Unit) {
        continuation(value)
    }

    private fun plusOne(value: Int, continuation: (Int) -> Unit) {
        continuation(value + 1)
    }

    private fun double(value: Int, continuation: (Int) -> Unit) {
        continuation(value * 2)
    }
}

fun main() {
    CpsCalculator.calculate(5) { result ->
        log.info("Result: {}", result)
    }
}
```

Continuation은 Callback과 유사한 방식이다.

Callback은 추가로 무엇을 해야 하는 지를 호출하는 것이고 여러번 호출할 수 있다. 반면, Continuation은 최종적으로 로직의 제어를 넘기기 위해 한 번 호출된다는 차이가 있다.

코루틴은 내부적으로 CPS를 이용해 구현된다.

#### Contiunation

아래는 Kotlin coroutines에서 사용하는 `Continuation` 인터페이스이다.

```kotlin
public interface Continuation<in T> {
    public val context: CoroutineContext
    public fun resumeWith(result: Result<T>)
}
```

내부적으로 coroutineContext를 포함하고, `resumeWith()`는 마지막 suspend 함수의 결과를 전달받을 수 있게 해주는 함수이다.

코틀린 컴파일러는 아래와 같은 `suspend` 키워드가 있는 메서드가 있다고 가정했을 때

```kotlin
suspend fun execute(userId: Long, productIds: List<Long>): Order {
    // 1. 유저 조회
    val user = userService.findUserFuture(userId)
        .await()

    // 2. 상품 목록 조회
    val products = productService
        .findProductFlowable(productIds)
        .toList().await()
    
    // 5. 주문
    val order = orderService.createOrderMono(
        user, products
    ).awaitSingle()

    return order
}
```

컴파일러가 `Continuation`를 활용한 CPS 구조의 코드로 변환하고, 일시 중단(suspend), 재개(resume)이 가능한 형태로 만든다.
- suspend 키워드가 붙은 함수에 Continuation 인자 추가
- 내부에서 다른 suspend 함수를 실행할 때 소유하고 있던 Continuation을 전달

여기서 일시 중단과 재개 가능한 단위를 **코루틴(coroutine)**이라 한다.

참고로 위 변환 과정으로 인해 suspend가 없는 함수에서는 다른 suspend 함수를 호출할 수 없다. 전달할 Continuation이 없기 때문이다.



## 참고
- https://www.charlezz.com/?p=45962
- https://incheol-jung.gitbook.io/docs/study/undefined-4/1
## Coroutine Scope

CoroutineScope는 Coroutine들에 대한 Scope를 정의한다.
- 각 Coroutine들은 Scope를 가진다.
- Scope는 자식 Coroutine들에 대한 생명주기를 관리한다.
- 자식 Coroutine이 모두 완료되어야 Scope도 완료된다.
- 내부 CoroutineContext에 Job을 반드시 포함되어야 한다.

CoroutineScope는 CoroutineContext를 가지는 범위라고 생각하면 된다. Scope를 통해 자식 코루틴에게 CoroutineContext를 전파할 수 있는 것이다.

![img_7.png](img_7.png)

## Coroutine Builder

Coroutine Builder는 CoroutineScope로부터 Coroutine을 생성한다. 생성된 Coroutine은 비동기로 동작하게 된다.

대표적인 예시로 launch가 있다.

#### launch

launch의 동작은 아래와 같다.

```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    // 새로운 Context를 생성
    val newContext = newCoroutineContext(context)
    // 새로운 Context로 코루틴 생성
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```

아래 코드를 보자.

```kotlin
fun main() {
    runBlocking {
        val cs = CoroutineScope(EmptyCoroutineContext)
        log.info("job: {}", cs.coroutineContext[Job])

        val job = cs.launch {
            // coroutine created
            delay(100)
            log.info("context: {}", this.coroutineContext)
            log.info("class name: {}", this.javaClass.simpleName)
            log.info("parentJob: {}", this.coroutineContext[Job]?.parent)
        }
        log.info("start")
        job.join()
        log.info("finish")
    }
}
```

실행 결과는 아래와 같다.

```
40:43 [main] - job: JobImpl{Active}@229d10bd
40:43 [main] - start
40:43 [DefaultDispatcher-worker-1] - context: [StandaloneCoroutine{Active}@15439c7c, Dispatchers.Default]
40:43 [DefaultDispatcher-worker-1] - class name: StandaloneCoroutine
40:43 [DefaultDispatcher-worker-1] - parentJob: JobImpl{Active}@229d10bd
40:43 [main] - finish
```

launch를 실행한 Job을 launch 내부에서는 부모 Job으로 가지고 있는 것을 알 수 있다.

launch는 비동기적으로 동작한다. 하지만, `join()`이 완료될 때까지 suspend가 되어서 finish는 잡이 실행된 이후에 실행된다.

#### async

launch와 유사한 메서드로 async가 있다.

```kotlin
public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyDeferredCoroutine(newContext, block) else
        DeferredCoroutine<T>(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```

launch와의 유일한 차이는 Job을 반환하는 것이 아니라 Deffered를 반환하는 것이다.

Deffered 인터페이스는 아래와 같다.

```kotlin
public interface Deferred<out T> : Job {
    public suspend fun await(): T
}
```

Deffered는 `await()`을 통해 원하는 시점 반환하는 값에 접근할 수 있다.

## Structured concurrency

아래 비동기 코드를 보자.

```kotlin
private fun nonStructured() {
    log.info("step 1")
    CompletableFuture.runAsync {
        Thread.sleep(1000)
        log.info("Finish run1")
    }
    log.info("step 2")
    CompletableFuture.runAsync {
        Thread.sleep(100)
        log.info("Finish run2")
    }
    log.info("step 3")
}

fun main() {
    log.info("Start main")
    nonStructured()
    log.info("Finish main")
    Thread.sleep(3000)
}
```

실행 결과는 아래와 같다.

```
18:39 [main] - Start main
18:39 [main] - step 1
18:39 [main] - step 2
18:39 [main] - step 3
18:39 [main] - Finish main
18:39 [ForkJoinPool.commonPool-worker-2] - Finish run2
18:40 [ForkJoinPool.commonPool-worker-1] - Finish run1
```

비동기 코드는 실행하는 채로 흐름을 그대로 가져가고 있다.

다음은 코루틴 코드를 보자.

```kotlin
private suspend fun structured() = coroutineScope {
    log.info("step 1")
    launch {
        delay(1000)
        log.info("Finish launch1")
    }
    log.info("step 2")
    launch {
        delay(100)
        log.info("Finish launch2")
    }
    log.info("step 3")
}

fun main() = runBlocking {
    log.info("Start runBlocking")
    structured()
    log.info("Finish runBlocking")
}
```

아래는 실행 결과이다.

```
20:54 [main] - Start runBlocking
20:54 [main] - step 1
20:54 [main] - step 2
20:54 [main] - step 3
20:54 [main] - Finish launch2
20:55 [main] - Finish launch1
20:55 [main] - Finish runBlocking
```

비동기 코드와 결과가 다른 점코드의 마지막에 호출한 `Finish runBlocking`이 마지막에 호출된다는 것이다.

이를 **구조화된 동시성(Structured concurrency)** 이라 한다. `coroutineScope` 키워드를 사용함으로써 **자식 코루틴(별도 쓰레드의 동작들)이 모두 종료되어야 해당 코루틴이 끝난 것으로 처리**된다.

구조화된 동시성의 또 하나의 특징은 **cancel이 발생하면 자식 coroutine까지 전파**한다는 점이다.

아래 코드를 보자.

````kotlin
private suspend fun structured() = coroutineScope {
    launch {
        try {
            delay(1000)
            log.info("Finish launch1")
        } catch (e: CancellationException) {
            log.info("Job1 is cancelled")
        }
    }

    launch {
        try {
            delay(500)
            log.info("Finish launch2")
        } catch (e: CancellationException) {
            log.info("Job2 is cancelled")
        }
    }
    
    this.cancel()
}

fun main() = runBlocking {
    log.info("Start runBlocking")
    try {
        structured()
    } catch (e: CancellationException) {
        log.info("Job is cancelled")
    }
    log.info("Finish runBlocking")
}
````

`structured()`를 보면 마지막에 `cancel()`을 호출하고 있다. 아래는 실행 결과이다.

```
26:57 [main] - Start runBlocking
26:57 [main] - Job1 is cancelled
26:57 [main] - Job2 is cancelled
26:57 [main] - Job is cancelled
26:57 [main] - Finish runBlocking
```

내부 코루틴에서도 `CancellationException`이 발생해서 로그가 찍힌 것을 볼 수 있다.
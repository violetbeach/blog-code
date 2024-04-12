## 심화 기능

#### flow

코틀린은 Reactor의 `Flux`와 유사한 `Flow`를 제공한다. Flow를 사용하여서 block 내에서 suspend 함수를 실행한다.

```kotlin
private fun range(n: Int): Flow<Int> {
    return flow {
        for (i in 0 until n) {
            delay(100)
            emit(i)
        }
    }
}

fun main() = runBlocking {
    log.info("Start runBlocking")
    range(5).collect {
        log.info("item: {}", it)
    }
    log.info("Finish runBlocking")

}
```

`emit`을 통해 값을 전달하면 받는 쪽에서 `collect`를 사용해서 item을 사용할 수 있다.

Flux처럼 다양한 연산도 제공하고 있다.
- 중간 연산자 - map, flatMap, take, drop, transform
- 종료 연산자 - collect, toList, toSet, reduce, fold, first, single

#### channel

채널은 파이프라인을 생각하면 된다. 채널은 아래 특징을 가진다.
- send와 receive가 가능하다.
- 여러 coroutine, thread에서 동시에 실행해도 안전하다.
- capacity와 BufferOverflow 인자를 전달하여 크기를 조절할 수 있다.

아래 코드를 보자.

```kotlin
suspend fun main() = runBlocking {
    val channel = Channel<Int>()
    launch {
        delay(100)

        for (i in 0 until 5) {
            channel.send(i)
        }
        channel.close()
    }

    delay(500)

    for (i in channel) {
        log.info("item: {}", i)
    }
}
```

for를 사용해서 channel의 값을 꺼내어서 사용할 수 있다.

Channel을 사용하면 실시간으로 값을 공유하고 전달받아야 할 때 유용하게 사용할 수 있다.
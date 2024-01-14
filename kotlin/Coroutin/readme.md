## 코루틴

코루틴(Coroutine)은 일시 중단이 가능한(Suspendable) 작업 객체이다.

## vs Thread

코루틴은 **경량 쓰레드**라고 부른다.

아래는 여러 개의 쓰레드로 여러 개의 작업을 실행하는 방식이다.

![img_1.png](img_1.png)

코루틴은 작업 하나하나에 Thread를 할당하는 것이 아니라 **Object**를 할당학고, 쓰레드가 Object를 스위칭함으로써 Context Swiching 비용을 대폭 줄인다.

![img_2.png](img_2.png)

## suspend 키워드

suspend는 코루틴 안에서만 실행할 수 있는 코루틴 전용 메서드에 사용한다.

![img.png](img.png)

suspend를 사용하면 해당 작업을 suspend 시키고, 그 시간 동안 다른 작업에 Thread를 할당할 수 있다.

#### sequence

아래는 map에 대한 결과를 저장하는 임시 컬렉션이 생성되고, filter의 실행 결과를 저장한다.

```kotlin
val resultList = people.map(Person::name).filter { it.startsWith("A") }
```

sequence를 사용하면 임시 컬렉션을 사용하지 않고, 컬렉션을 연쇄할 수 있다.

```kotlin
people.asSequence().map(Person::name).filter { it.startsWith("A") }.toList()
```

## CoroutineContext

코루틴을 어떤 Thread에게 보낼 지 결정하는 컴포넌트를 Dispatcher라고 한다.
- Dispatcher.Default
  - 리스트를 정렬하거나 Json Parsing 등 가공 작업에 주로 사용
  - CPU를 많이 사용하는 무거운 작업에 최적화
  - CPU 개수 만큼 스레드를 생성
- Dispatcher.Main
  - 화면 UI 작업을 위해 사용
- Dispatcher.IO
  - 네트워크 DB 작업할 경우 사용
  - 읽기, 쓰기 작업에 최적화
  - Thread를 Block할 필요가 있는 경우

## Scope

코루틴은 **구조화된 도잇성 원칙**을 따른다. 코루틴은 수명을 제한하는 특정 **코루틴 Scope** 내에서 실행되어야 하는 원칙이다.

코루틴 스코프는 스코프 내의 코루틴이 모두 완료될 때까지 종료되지 않으므로 코루틴이 손실되지 않는 것을 보장한다.

- GlobalScope
- ViewModelScope
- LifecycleScope
- LiveData

## Context

CoroutineContext는 코루틴을 어떤 쓰레드에서 실행할 것인지에 대한 동작을 정의하고 제어한다.

#### runBlocking

Main Thread를 Blocking 한 채 구문 내 작업을 새 Thread에 할당하여 수행한다.

일반적으로 다수의 async {}로 구성하는데, 이 경우 해당 작업들이 모두 완료되고 반환되면 Main Thread의 Blocking을 해제한다.

#### launch

Main Thread를 Unblocking 한 채 {} 구문 내 작업을 수행한다.

#### async

Main Thread를 Unblocking 한 채 {} 구문 내 작업을 수행하고 결과 값을 반환한다.

#### withContext

코루틴 중 Context를 변경할 때 사용한다.

아래는 withContext를 사용하지 않는 코드이다. 

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    CoroutineScope(Dispatchers.Main).launch {
        exampleSuspend()
    }
}

suspend fun exampleSuspend() {
    val job = CoroutineScope(Dispatchers.IO).async {
        delay(1000)
    }

    job.await()
}
```

아래는 withContext를 사용한 코드이다.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    CoroutineScope(Dispatchers.Main).launch {
        exampleSuspend()
    }
}

suspend fun exampleSuspend() {
    withContext(Dispatchers.IO) {
        delay(1000)
    }
}
```

withContext도 suspend 함수이기 때문에 withContext가 전부 실행될 때까지 정지된다.

즉, withContext는 async와 다르게 병렬 처리가 불가능하고 순차 처리만 가능하다.

## 참고
- https://www.charlezz.com/?p=45962
- 
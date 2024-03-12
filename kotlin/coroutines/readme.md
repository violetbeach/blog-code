## 코루틴

코루틴(Coroutine)은 Co(함께, 서로) + routine(규칙적 작업의 집합) 2개가 합쳐진 단어로 함께 동작하며 규칙이 있는 작업의 집합을 의미한다.

왜 `Koroutine`이 아니라 `Coroutine`인지 의아할 수 있는데 코루틴은 코틀린만의 것이 아니다. Python, C#, Go, Javascript 등 다양한 언어에서 지원하는 개념이다.

JS의 async, await도 동일한 개념이고 코루틴은 프로그래밍 초창기부터 존재하던 개념이다.

## 코틀린

코틀린에서는 코루틴을 위한 공식 라이브러리(`kotlinx.coroutines`)를 지원한다.

아래는 Kotlin Coroutines에 대한 설명이다.
- 동시성을 위한 기능을 제공
- Async Non-blocking으로 동작하는 코드를 동기 방식으로 작성할 수 있도록 지원
  - 코틀린 컴파일러에서 바이트 코드를 비동기 방식으로 변경
- CoroutineContext를 통해 Dispatcher, error handling, threadLocal 등을 지원
- CoroutineScope를 통해 structured concurrency, cancellation 제공
- flow, channel 등의 심화 기능 제공
## JAVA - CompletableFuture 사용해보기! (feat. Event?)

## CompletableFuture 이해

Java에서 비동기 작업의 처리 결과를 반환받을 수 있는 방법이 없을까?

Java5 버전에는 Future라는 인터페이스가 추가되면서 비동기 작업에 대한 결과를 반환 받을 수 있게 되었다. 단, 아래의 한계가 있었다.
- 외부에서 완료시킬 수 없다.
- 블로깅 코드(get)를 통해서만 이후의 결과를 처리할 수 있다.
- 여러 Future를 조합할 수 없다.
- 예외 처리를 할 수 없다.

그래서 Java8에서는 위 문제들을 모두 해결한 CompletableFuture가 등장했다.

CompletableFuture는 Future, CompletionStage 인터페이스를 모두 구현하고 있기에 콜백 형태로 동작이 가능하다.
- (ex. ~초 이내에 완료되지 않으면 기본 값 반환, 예외 처리 등)

## CompletableFuture 사용

CompletableFuture의 기본적인 메서드는 아래와 같다.

`runAsync()`와 `supplyAsync()`는 Java7에 추가된 ForkJoinPool을 사용해서 작업을 실행할 쓰레드를 얻어 실행시킨다.

- runAsync
  - 반환 값이 없는 경우

- supplyAsync
  - 반환 값이 있는 경우

해당 메서드 이외에도 CompletableFuture에는 아래와 같은 기능을 제공한다.

**[Callback]**
- thenApply
  - 반환 값을 받아서 다른 값으로 반환
- thenAccept
  - 반환 값을 처리
- thenRun
  - 반환 값을 받지 않고 다른 작업을 실행함

**[Combine]**
- thenCompose
  - 두 작업을 이어서 실행하도록 조합
  - 앞선 작업의 결과를 받아서 사용할 수 있음
- thenCombine
  - 두 작업을 독립적으로 실행
  - 둘 다 완료되었을 때 콜백을 실행
- allOf
  - 여러 작업들을 동시에 실행
  - 여러 작업들이 완료된 시점을 알 수 있다.
- anyOf
  - 여러 작업들 중에서 가장 빨리 끝난 하나의 결과에 콜백을 실행

**[Action]**
- complete
  - **작업이 완료되지 않았다면** 특정 값으로 변경한다.

단, 조합을 할 때는 스레드 풀의 스레드 수와 작업 수를 잘 조율하지 않으면 데드락이 발생할 수 있다.

아래는 해당 이슈를 잘 정리한 포스팅이다.
- https://techblog.woowahan.com/2722

## 참고
- https://mangkyu.tistory.com/263
- https://stackoverflow.com/questions/38254268/completablefuture-exceptionally-with-executor
- https://techblog.woowahan.com/2722/

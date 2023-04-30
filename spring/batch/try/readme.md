## Spring Batch - 반복 및 실패 처리하는 방법!

## 1. Repeat

특정 조건에 의해서 Job / Step을 반복할 수 있도록 배치 애플리케이션을 구성할 수 있다.

SpringBatch에서는 이러한 반복을 RepeatOperation(interface)를 사용해서 처리하고 있고, 기본 구현체로 RepeatTemplate을 제공한다.

## 2. FaultTolerant

Spring Batch는 Job 실행 중에 오류가 발생할 경우 장애를 처리하기 위한 기능을 제공한다.

예외가 발생해도 Step이 즉시 종료되지 않고, Retry 혹은 Skip 기능을 활성화해서 복원력을 향상시킬 수 있다.

## 3. Skip

## 4. Retry

## 5. Skip & Retry 아키텍처
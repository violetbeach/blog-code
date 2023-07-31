## Stream?

Stream의 정의는 무엇일까?

>  stream is a sequence of elements on which we can perform different kinds of sequential and parallel operations

오라클 공식 문서에 따르면 **순차 및 병렬적인 집계연산을 지원하는 연속된 요소**라고 한다.

## Java Stream API

이번엔 자바 스트림 API에서 java.util.stream에 대해 알아보자.

> Classes to support functional-style operations on streams of elements, such as map-reduce transformations on collections.

공식 자바독에 따르면 해당 패키지는 요소들의 스트림에 함수형 스타일의 연산을 지원하는 클래스들이다.

### 생성

Stream은 Collection, Array, File, Thrid-party 라이브러리 등 에서 생성할 수 있다.

## Intermediate Operation

스트림에서 filter, map, peek, sorted, distinct, limit 등 중간 연산을 할 수 있다. 이러한 기능은 **Lazy Evaluation**을 가능케 한다.

즉, 최종 연산이 들어오기 전까지 중간 연산은 실행되지 않는다.

이러한 중간 연산은 Stateful, Stateless로 나뉜다.

가령, map의 경우 자기 자신의 인자만 알면 되지만, sorted의 경우 다른 값의 상태까지 신경을 써야 한다.

## Terminal Operation

최종 연산자는 collect, findAny, findFirst, forEach, ...을 말한다.


## https://www.youtube.com/watch?v=rbm87IFpwvQ
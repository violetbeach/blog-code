## Java의 버전 별 주요한 기능 및 특징

해당 포스팅에서는 각 자바의 버전별 주요한 기능이나 특징에 대해 정리한다.

## 자바의 버전

자바 Version은 다양한데 어떤 Version을 사용해야 할까..? 현재 아래와 같이 많이 사용하고 있다.
- 기존 기업의 레거시한 프로젝트의 경우 Java 8 혹은 Java 1.5 등을 사용하는 경우도 많다.

자바의 경우는 Python2/Python3과는 다르게 **하위 호환성**이 매우높다. 그래서 LTS(현재 기준 17) 버전으로의 업그레이드 등은 자유롭게 선택할 수 있다.

## 특징

아래는 Java의 버전 별 주요한 패치 내용에 대해서 다룬다.

 각 기능에 대해서 다루면 포스팅의 양이 방대해지므로 목록화(?) 정도만 작성한다.

### Java 8

- Lambda
- Stream
- Interface default method
- Optional
- Date and Time API (JodaTime - LocalDateTime, ...)

### Java 9

- 모듈 시스템 등장(jigsaw)
- Optional 보완 (ifPresentOrElse, ...)
- Interface에 private method 사용 가능

### Java 10

- var 키워드 도입 (로컬 변수 유형 추론)
- 병렬 처리 GC 도입으로 인한 성능 항상
- JVM 힙 영역을 시스템 메모리가 아닌 다른 메모리에도 할당 가능

## Java 11

- Oracle JDK와 OpenJDK 통합, ... (기타, 생략)
- 람다 표현식에 var 키워드 사용 가능

## Java 12

- 큰 이슈 없음

## Java 13

- 스위치 표현식 (람다 스타일 구문) 지원

## Java 14
 
- 스위치 표현식 표준화
- record (data object) 도입

## Java 15

- Text-Blocks (Multiline Strings) 지원
- Sealed class 도입 (상속 클래스 제한)
- ZGC 추가

## Java 16

## Java 17






## 참고
- https://velog.io/@ljo_0920/java-%EB%B2%84%EC%A0%84%EB%B3%84-%EC%B0%A8%EC%9D%B4-%ED%8A%B9%EC%A7%95
- https://www.marcobehler.com/guides/a-guide-to-java-versions-and-features
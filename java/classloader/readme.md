## JVM - 클래스 로더 이해하기!

<아래는 이전 포스팅에서 JVM 구성 요소를 정리한 것이다.>
- https://jaehoney.tistory.com/173

자바는 동적으로 클래스를 읽어오기 때문에 프로그램이 실행중인 런타임에서 JVM과 연결된다.


한 번에 메모리에 모든 클래스를 로드하는 것이 아닌, 필요한 순간에 해당 클래스(.class) 파일을 찾아 메모리에 로딩해주는 역할을 하는 것이 바로 클래스 로더(class loader) 이다.

이에 대해 더 자세히 알아보자.

## 동작 세 단계

클래스 로더의 동작은 아래 세 가지 단계로 나눌 수 있다.
- 로딩
  - 자바 바이트 코드(.class)를 Method area에 저장한다.
  - 로드된 클래스 및 부모 클래스의 정보
  - 변수나 메서드 정보
- 링크
  - 검증 - 클래스가 자바 명세 및 JVM 명세에 명시된 대로 잘 구성되어 있는 지 검사
  - 준비 - 클래스가 필요한 메모리 할당 및 정의된 필드, 메소드 등 할당
  - 분석 - 심볼릭 메모리 레퍼런스를 Method area 영역에 있는 실제 레퍼런스로 교체한다.
- 초기화
  - 클래스 변수들을 적절한 값으로 초기화한다.

## 부모 클래스 로더와 자식 클래스 로더

각각의 클래스 로더는 부모 클래스로더가 있다.

클래스를 로드할 때는 자식 클래스 로더가 이미 메모리에 로딩된 클래스가 있는 지 확인한 후, 없다면 부모 클래스 로더에게 위임한다.

부모 클래스 로더도 못찾으면 그때는 자식 클래스 로더가 해당 클래스를 찾게 된다.



## 참고
- https://steady-coding.tistory.com/593
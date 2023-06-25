## Spring - Environment 동작원리 이해하기!

기존 자바 애프리케이션의 경우 커맨드 라인 인수, 자바 시스템 속성 인수, OS 환경변수 등을 꺼내는 방법이 달랐다.

- OS 환경 변수: System.getEnv(key)
- 자바 시스템 속성: System.getProperty(key)
- ...

스프링은 이를 추상화해서 PropertySource라는 추상 클래스를 만들고, 그것을 구현하는 `XXXPropertySource`라는 구현체를 만들었다.

![img_1.png](img_1.png)

여기서 설정 데이터란 우리가 흔히 말하는 `application.properties`나 `application.yml` 파일 등을 말한다.

그래서 내부적으로 스프링 컨테이너 로딩 시점에 `PropertySource`들을 생성하고, `Environment`에서 사용할 수 있게 연결해둔다.
- PropertySource 리스트를 하나씩 접근해서 읽으면서 환경을 찾으면 반환하는 방식
- 내부적으로 우선순위를 정했다.
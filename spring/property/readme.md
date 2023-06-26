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
  - https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config

#### properties, yml

`properties`와 `yml`의 경우 보통 MainSource에서 가까운 파일을 먼저 읽어오는 것으로 알고 있다.

실제로 설정 데이터의 경우 아래의 우선순위를 가진다.
- jar 내부 `application.properties`
- jar 내부 `application-{profile}.properties`
- jar 외부 `application.properties`
- jar 외부 `application-{profile}.properties`

그런데 한 파일 안에서는 어떻게 우선순위가 될까? 아래 `application.properties`를 보자.

```properties
url=local.db.com
username=local_user
password=local_pw
#---
spring.config.activate.on-profile=dev
url=dev.db.com
username=dev_user
password=dev_pw
#---
spring.config.activate.on-profile=prod
url=prod.db.com
username=prod_user
password=prod_pw
```

단일 파일로 설정 데이터를 통합했을 경우 속성의 순서가 중요하게 될 수 있다.

가령, default(local) 설정을 가장 아래쪽으로 내린다면 dev와 prod의 설정은 무시된다.

사실 스프링은 단순하게 문서를 **위에서 아래로 순서대로 읽으면서** 설정을 등록하기 때문이다.
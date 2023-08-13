k8s, 리버스 프록시 방식 등을 통해 무중단 배포를 하게 된다.

기존 인스턴스를 종료했을 때 이미 수행중인 작업이 있는 지 어떻게 보장할 수 있을까..?

## Graceful Shutdown

Shutdown Graceful은 '우아한 종료'라는 뜻으로 하던 작업을 모두 안전하게 처리한 후 프로세스를 종료하는 것을 말한다.

Linux의 `kill - 15` 명령어로 Graceful Shutdown을 할 수 있다고 말하는데, 실제 Spring 애플리케이션을 보면 하던 작업들이 강제로 종료된다.

SpringApplication에서 요청을 어떻게 처리하는 지 Linux에서는 알 수 없기 때문이다!

## SpringBoot

`SpringBoot 2.3` 부터는 손쉽게 Graceful Shutdown을 할 수 있는 설정을 제공한다.

```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=20s
```

`server.shutdown`은 graceful과 immediate가 있고 **default**가 **immediate**이다. 이를 **graceful**로 명시하면 스프링에서는 신규 요청은 받지 않는 채 기존 작업에 대해서 안전하게 처리한 후 종료한다.

`spring.lifecycle.timeout-per-shutdown-phase`는 graceful shutdown을 기다릴 Time-out 이라고 이해하면 된다. 해당 설정이 10초라면 10초 뒤에는 작업이 존재해도 앱이 종료된다.

## 참고

- https://www.baeldung.com/spring-boot-web-server-shutdown
- https://www.baeldung.com/spring-boot-graceful-shutdown
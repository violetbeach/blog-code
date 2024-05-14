## Spring Cloud

안정적인 Micro Service를 만들고, 외부 환경에 대해 신경쓰지말고 내부 로직에만 집중할 수 있게 도와주는 라이브러리이다.

Spring Cloud에서 지원하는 무수한 기능 중 대표적인 3가지는 다음과 같다.

#### API Grateway

- 각 시스템에서 외부 API가 어떤 것인지 알 필요가 없어진다. (특정 시스템으로의 강결합이 제거된다.)

#### Circuit breaker

- 외부의 장애를 격리하고 시스템 안정성을 유지할 수 있다.
- 장애 복구 시간을 확보할 수 있다.

#### Spring Cloud Stream

- 추상화된 발행/구독, 생산/소비 메시지 패턴을 제공한다.

Kafka를 사용한다고 가정했을 때 비즈니스 코드에 카프카 코드가 들어올 수 있다. 즉, 시스템으로의 강결합 때문에  Kafka를 다른 MQ로 교체하는 등의 처리가 어려워진다.

Spring Cloud Stream을 사용하면 강결합을 방지하고 로직 변경 없이 카프카를 다른 MQ로 교체하는 것이 가능해진다.

## Circuit breaker

Circuit breaker는 전기 회로의 차단기와 같은 역할을 하는 디자인 패턴을 말한다.

특정 서비스의 과부하나 장애가 발생했을 때 복구가 될 때까지 추가적인 요청을 차단해서 시스템의 안정성을 유지한다.

그래서 Reactive Systems의 Resilient(복원력) 지원한다고 보면 될 것 같다.

Spring Cloud는 Spring Cloud Circuit Breaker라는 라이브러리를 지원한다.
- Spring Cloud Circuit Breaker의 구현체로는 Resilience4j와 Spring Retry를 제공한다.
- 해당 포스팅에서는 Resilience4j에 대해서만 다룬다.

#### Resilience4j

![img.png](img.png)

Resilience4j는 Java에서 Curcuit breaker를 지원하는 러이브러리이다.

Gradle에 아래 의존을 추가한다.
```groovy
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.8")
    }
}
```

### Circuit breaker 상태

Circuit breaker는 아래 상태가 존재한다.
- Closed: 정상적으로 요청을 받을 수 있는 상태 (Open으로 상태 이동 가능)
- Open: Curcuit breaker가 작동하여 목적지로 가는 트래픽, 요청을 막고 fallback을 반환 (Half Open으로 상태 이동 가능)
- Half Open: 트래픽을 조금씩 흘려보고 Open을 유지할지 Closed로 변경할 지 결정(Open, Closed로 상태 이동 가능)

#### Closed

Closed 상태는 가장 기본적인 상태이다.
- 들어오는 모든 요청을 대상 메서드, 서비스에 전달
- 서비스에 전달 후 응답이 느리거나 error가 발생한다면 fallback을
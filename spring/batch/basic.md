## Spring - 스프링 배치(Spring Batch)란 무엇인가?

## 스프링 배치

자바에서 표준으로 제공하는 일괄 처리(배치)를 위한 기술이 존재하지 않았었다.

스프링 배치(Spring Batch)는 이러한 문제를 해결하기 위해서 탄생했으며, Spring Source와 Accenture가 공동으로 개발했다. (Accenture는 배치 아키텍처를 구현하면서 쌓은 기술적인 경험과 노하우를 가진 기업)

### 배치

아래의 경우에 배치를 효율적으로 사용할 수 있다.

- 주기적으로 배치 프로세스를 커밋
- 적당한 양의 데이터로 분할 처리
- 동시 다발적인 Job의 병렬 처리
- 의존 관계가 있는 step 여러 개를 순차적으로 처리
  - 조건적 Flow 구성을 통한 체계적이고 유연한 배치 모델 구성
- 반복, 재시도, Skip 처리

### 배치 핵심 프로세스

아래는 배치에 가장 주요한 세 가지 단계이다.
- Read - 데이터베이스, 파일, 큐에서 다량의 데이터를 조회한다.
- Process - 특정 방법으로 데이터를 가공한다.
- Write - 가공된 데이터를 필요에 따라 저장하거나 사용한다.

## Spring Batch Layer

아래는 Spring Batch에서 공식적으로 제공하는 Layer의 그림이다.

![img.png](img.png)

[Application]
- Spring batch를 사용해서 개발자가 필요에 맞게 구현한 Job과 커스텀 코드
- 개발자는 Application 구현에만 집중하고, 코어한 기능은 프레임워크에 위임한다.

[Batch Core]
- Job을 실행, 모니터링, 관리하는 API 등을 말한다.
- JobLauncher, Job, Step, Flow과 같은 클래스를 말한다.

[Batch Infrastructure]
- Application, Core 모두 해당 Infrastructure 위에서 빌드된다.
- Job의 실행 흐름과 처리를 위한 틀을 제공
- Reader, Processor, Writer, Skip, Retry 등을 말한다.

## 설정 클래스

Spring Batch를 사용하려면 아래의 애노테이션을 Configuration에 등록하면 된다.

```java
@EnableBatchProcessing
```

결과적으로 스프링 배치의 실행 및 초기화가 이루어진다.

이때 총 3개의 설정 클래스가 설정으로 등록된다.

#### 1. BatchAutoConfiguration

- 스프링 배치가 초기화 될 때 자동으로 실행되는 설정 클래스
- Job을 수행하는 JobLauncherApplicationRunner 빈을 생성

#### 2. SimpleBatchConfiguration
- JobBuilderFactory와 StepBuilderFactory 빈 생성
- 스프링 배치의 주요 구성 요소 생성 (프록시 객체)

#### 3. BatchConfigurerConfiguration

아래 두 개의 설정 클래스를 가진다.

- BasicBatchConfigurer
  - SimpleBatchConfiguration에서 생성한 프록시 객체의 실제 대상 객체를 생성하는 설정 클래스
- JpaBatchConfigurer
  - BasicBatchConfigurer를 상속받는다.
  - JPA 관련 객체를 생성하는 설정 클래스

실행 순서는 아래와 같다.

![img_1.png](img_1.png)

해당 클래스들은 BatchConfigurer 인터페이스를 구현한다. 즉 커스텀으로 직접 구현해서 등록하는 것도 가능하다.

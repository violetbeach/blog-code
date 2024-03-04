최근에 팀에서 모니터링이 점점 어려워지는 문제가 발생하고 있다. 가장 큰 문제는 **불필요한 에러 Alert이 너무 많다는 것**이다.

그 결과 에너지를 더 많이 쏳게 되고, 정말 받아야 하는 Alert이 왔을 때 무신경하게 대응하게 된다.

팀원들이 너무 바쁘기도하고, 내가 맡게 될 프로젝트의 중요도와 Risk, 트래픽 등을 고려했을 때 모니터링 개선이 반드시 필요해서 시간을 내서 학습하게 되었다.

## Sentry

Sentry은 에러 모니터링 및 성능 모니터링을 제공해주는 돋구이다. 주로 에러 트래킹이나 Slack 등을 통한 Alert으로 많이 사용한다.

학습을 위해 SpringBoot 3.1.9 버전과 아래 라이브러리를 사용했다.

```groovy
implementation 'io.sentry:sentry-spring-boot-starter-jakarta:7.5.0'
``` 

yml은 아래와 같이 설정할 수 있다.

```yaml
sentry:
  dsn: https://fa1b1dc87e3eb8bee49cc2d25b06615e@o4506869937078272.ingest.us.sentry.io/4506869938913280
```

해당 키는 Sentry에 로그인하면 발급받을 수 있다.

테스트를 해보자.

```kotlin
@RestController
class Controller {

    @RequestMapping("/test")
    fun test() {
        throw RuntimeException("Error!!")
    }
}
```

이제 API를 호출하면 아래와 같이 Sentry로 예외가 전달된다.

![img.png](img.png)

## Log 기준

아래처럼 Exception을 감싸면 Sentry로 전달되지 않는다.

```kotlin
@RestController
class Controller {
    
    @RequestMapping("/test")
    fun test() {
        try {
            throw RuntimeException("catch!!")
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
```

우선 Error를 전달(Capture)하는 기준에 대해 알아야 한다.

## 설정

설정은 프로퍼티나 YML 말고도 JVM 언어 등 프로그래밍 언어로도 가능하다.

아래는 `.properties`를 사용한 예시이다.

```properties
# 추적할 Event의 비율을 설정한다. 1.0이면 100%를 캡처한다.
# (너무 높으면 리소스를 많이 사용한다.)
sentry.traces-sample-rate=1.0
# Event를 전송할 확률을 설정한다. 1.0이면 100%를 캡처한다.
# 발생시킬 Event의 태그를 지정
sentry.tags.first_tag=first-tag-value
# 무시할 Exception 정의
sentry.ignored-exceptions-for-type=java.lang.RuntimeException,java.lang.IllegalStateExceptio
# 디버그 모드 (콘솔에 정보 출력)
sentry.debug=true
# 디버그 모드의 로그 레벨 설정 (debug, info, warning, error, fatal - default: debug)
sentry.diagnosticLevel=debug
# 로컬에 저장할 envelopes 수
sentry.maxCacheItems=30
# Stack Trace를 모든 메시지에 첨부
sentry.attachStacktrace=true
# HTTP 요청 본문 캡처 여부 (never, small, medium, alwways)
sentry.maxRequestBodySize=never
# SDK가 Sentry에 이벤트를 보낼 지 여부
sentry.enable=true
# 이벤트를 전송하기 전에 호출할 함수
sentry.beforeSend: null
# 예외 해결 순서 지정, -2147483647로 설정하면 Spring 예외 처리기에서 처리된 오류는 무시한다.
sentry.exception-resolver-order: 0
...
```

## 참고

- https://docs.sentry.io/


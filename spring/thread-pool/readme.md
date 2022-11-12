## Spring Boot - Thread Pool을 관리하는 방법! (Spring Boot에서의 Thread 동작) [ThreadPoolExecutor]

ThreadPool의 동작을 이해하는 것이 개발하는 앱 서버에 영향을 미칠까..?!

ThreadPool은 응답 시간, 처리 속도, TPS에 영향을 미친다.

추가로 병목 현상, CPU 오버헤드, 메모리 부족 등의 문제를 방지해서 안정적인 어플리케이션의 운용이 가능하게 한다.

즉, ThreadPool에 대한 기본적인 개념을 알아야 더 좋은 Server Application을 개발할 수 있다.

### Java - Threading Model

Java에서는 One-to-One Threading-Model로 Thread를 생성합니다.

![img_1.png](img_1.png)

One-to-One Threading-Model은 User Thread(Process의 스레드) 1개는 OS Thread 1개와 연결해야 합니다.

이때 작업 요청이 들어올 때마다 Thread를 생성하는 비용이 추가적으로 들어가니 비효율적입니다.

추가로 생성 비용 뿐만아니라 Thread가 무제한적으로 생성되면서 메모리 부족이 발생할 수 있고 잦은 Context-Switching으로 CPU 오버헤드 등이 발생할 수 있습니다.

그래서 Java에서도 Thread Pool을 사용하게 됩니다.

## Thread Pool

Spring Boot에서 Thread Pool의 동작은 아래와 같습니다.

![img.png](img.png)

내장 Tomcat에 Thread Pool을 만들고, Request가 들어오면 해당 Thread가 해당 요청을 담당해서 처리합니다.

쓰레드는 Connection Pool에서 유휴 상태인 DB Connection이 존재하면 사용하고, 생성해야 한다면 생성합니다.

그리고 DB Connection을 이용해서 DB에서 쿼리를 날린 뒤 사용자에게 결과를 반환하게 됩니다.

### ThreadPoolExecutor

더 자세히 알아보겠습니다.

스프링 부트에서 멀티 쓰레드(Multi thread)를 관리하기 위해서 ThreadPoolExecutor를 사용합니다.

Spring Boot가 실행되면 내부적으로 ThreadPoolExecutor 구현체를 생성해서 내장 톰캣이 사용할 쓰레드 풀을 생성하는 구조입니다.

application.yml에 아래와 같이 쓰레드 풀 프로퍼티를 추가할 수 있습니다.

```
server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
    accept-count: 100
  port: 8080
```

위의 설정은 스프링부트 default로 사용되는 설정입니다.

-   threads.max - 쓰레드의 최대 개수
-   threads.min-spare - 활성화 상태로 유지할 최소 쓰레드의 개수
-   tomcat.accept-count - 모든 스레드가 사용 중일때 들어오는 연결 요청 큐의 최대 길이

더 상세한 설정은 공식문서의 application-properties 부분을 참고하시면 됩니다.

[https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)

Spring Boot는 해당 설정이 있다면 가져와서 ThreadPoolExecutor 빈(tomcatThreadPool)을 등록할 때 사용합니다.

---

**Reference**

-   [https://velog.io/@sihyung92/how-does-springboot-handle-multiple-requests](https://velog.io/@sihyung92/how-does-springboot-handle-multiple-requests)
-   [https://d2.naver.com/helloworld/5102792](https://d2.naver.com/helloworld/5102792)
-   [https://www.baeldung.com/java-web-thread-pool-config](https://www.baeldung.com/java-web-thread-pool-config)

​
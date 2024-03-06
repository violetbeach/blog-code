### 빈 라이프 사이클

**스프링 컨테이너**는 **초기화**와 **종료**라는 **라이프사이클**을 갖습니다. 아래 코드를 보면 컨텍스트 객체를 생성하고 스프링 컨테이너를 초기화합니다.

```java
// 1. 컨테이너 초기화
AnnotationConfigApplicationContext ctx = 
    new AnnotationConfigApplicationContext(AppContext.class);

// 2. 컨테이너에서 빈 객체를 구해서 사용
Greeter g = ctx.getBean("greeter", Greeter.class);
g.method1();

// 3. 컨테이너 종료
ctx.close();
```
이 때 스프링 컨테이너는 설정 클래스에서 정보를 읽어와 알맞은 빈 객체를 생성하고 각 빈을 연결(의존 주입)하는 작업을 수행합니다.

그 후 컨테이너를 이용해서 빈 객체를 구해서 객체를 사용합니다. 컨테이너 사용이 끝나면 컨테이너를 종료합니다.

아래는 **빈의 생성 주기**입니다.

1. Container Start: 컨테이너 초기화
2. Bean Instantiated: 빈 객체의 생성
3. Dependencies Injected: 빈 의존 주입
4. init method: 빈 객체의 `initMethod` 실행
5. Bean usage: 빈 사용

아래는 **빈의 소멸 주기**입니다.

1. destroy method: 빈 객체의 `destroyMethod` 메서드 실행
2. Bean Destruction: 빈 소멸 (사용한 리소스 정리, 연결 해제 등)
3. Container exit: 컨테이너 종료

### initMethod, destroyMethod

```java
public class Client implements InitializingBean, DisposableBean {

    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Client.afterPropertiesSet() 실행");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Client.destroy() 실행");
    }

}
```

**빈 객체**는 초기화할 때 `InitializingBean` 인터페이스의 **`afterPropertiesSet()`** 를, 소멸할 때는 `DisposableBean` 인터페이스의 **`destroy()`** 를 호출합니다.

다른 방법으로는 `@Bean` 애노테이션의 `initMethod`, `destoryMethod` 설정을 사용할 수 있습니다.

```java
@Bean(initMethod = "connect", destroyMethod = "close")
public Client client() {
    Client client = new Client();
    client.setHost("host");
    return client;
}
        
public class Client {
    private String host;

    public void connect() {
        System.out.println("Client.connect() 실행");
    }

    public void close() {
        System.out.println("Client.close() 실행");
    }

}
```

최근에는 스프링에서 권장하는 방법은 스프링에 종속적인 기술이 아닌 JSR-250에 속한 아래 애노테이션입니다.

```java
@Component
class MySpringBean {

  @PostConstruct
  public void postConstruct() {
    //...
  }

  @PreDestroy
  public void preDestroy() {
    //...
  }
}
```

### 빈의 스코프

별도의 설정을 하지 않으면 빈은 싱글톤 범위를 갖습니다.

**싱글톤 범위의 빈 말고 프로토타입 범위의 빈을 설정할 수**도 있습니다. 빈의 범위를 **프로토타입으로 지정**하면 빈 객체를 구할 때마다 **매번 새로운 객체를 생성**합니다.

```
@Bean
@Scope("prototype")
public Client client() {
    Client client = new Client();
    client.setHost("host");
    return client;
}
```

**프로토타입 범위**를 갖는 빈은 **라이프사이클**이 싱글톤과 다른 점을 주의해야 합니다.

스프링 컨테이너는 프로토타입의 빈 객체를 생성하고 프로퍼티를 설정하고 초기화 작업까지는 수행하지만, 컨테이너를 종료한다고 해서 생성한 프로토타입 빈 객체의 소멸 메서드를 실행하지는 않습니다. 즉, **소멸 처리**는 코드에서 **직접 구현**해야 합니다.

```java
if (prototypeBean instanceof DisposableBean) {
    ((DisposableBean) prototypeBean).destroy();
}
```

소멸 처리는 위와 같이 `destroy()`를 직접 호출하도록 구현할 수 있습니다.

감사합니다.

## 참고

- https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/BeanFactory.html
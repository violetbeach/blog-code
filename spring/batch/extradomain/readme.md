## BatchStatus / ExitStatus

- BatchStatus는 직접 제어할 수 없으며, Enum 값으로 되어 있다.
- ExitStatus는 String으로 되어 있고, 직접 Custom으로 만들 수 있고 제어도 가능하다. 그래서 Flow를 구성할 때 Transition을 구성하는 데 사용할 수 있다.

## JobScope, StepScope

일반적으로 Scope는 Spring Bean의 생명 주기를 말한다.

```java
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
class SingletonBean{
    public SingletonBean(){
        System.out.println("SingletonBean Constructor");
    }
    
}
```

Spring에서 Scope는 Singletone, ProtoType, Request, Session, Application이 있다.

스프링 배치에서는 @JobScope와 @StepScope를 사용한다.
- default: @Scope(value = "job", proxyMode = ScopedProxyMode.TARGET_CLASS)
  - 앱 구독시점이 아닌 빈의 실행시점에 빈이 생성된다.
  - @Values 를 주입해서 빈의 실행 시점에 값을 참조해서 실행이 가능해지기 때문
  - @Value("#{jobParameters[param]}") ,@Value("#{jobExecutionContext[param]}"), @Value("#{stepExecutionContext[param]}")
- Thread-safty하다.


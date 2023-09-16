최근에 AOP를 비롯해서 Spring 기술을 정확하게 모른다는 생각이 들었다.

실무만 장애없이 처리할 수 있는 수준으로 구사할 수 있다.

그래서 Spring에 대해 더 자세히 알고 사용하고 싶고 더 깊은 레벨로 문제를 근본적으로 해결하고 싶어서 공식 문서를 정독하기로 했다.

아래는 Spring 공식 문서를 정독한 것을 기록한 것이다.

## 핵심 기술

> Foremost amongst these is the Spring Framework’s Inversion of Control (IoC) container. A thorough treatment of the Spring Framework’s IoC container is closely followed by comprehensive coverage of Spring’s Aspect-Oriented Programming (AOP) technologies. The Spring Framework has its own AOP framework, which is conceptually easy to understand and which successfully addresses the 80% sweet spot of AOP requirements in Java enterprise programming.

스프링의 수 많은 핵심 기술 중 가장 중요한 것은 `IOC Container`라고 소개한다. 그 뒤에 `AOP` 기술에 대한 내용이 밀접하게 관련되어 있다.

## Container

`ApplicationContext`는 `Spring IOC 컨테이너`를 의미하며 Bean의 인스턴스화, 구성 및 어셈블을 담당한다.

과거에는 `ClassPathXmlApplicationContext`나 `FileSystemXmlApplicationContext`의 인스턴스를 생성했지만 지금은 `Java 기반 구성`이나 `Groovy` 기반 구성을 사용하도록 컨테이너에 지시할 수 있다.

Spring 동작 방식은 아래와 같다.

![img.png](img.png)

## 새롭게 알게 되었거나 정확히 모르던 것

- 빈은 2개 이상의 이름을 가질 수 있다. `@Bean` 애노테이션을 사용할 경우 `name` 옵션을 `,`로 연결하면 된다.
- 빈은 정적 팩토리 메서드로도 생성할 수 있다.
- 지연 초기화를 사용하면 특수한 경우 성능을 Safe 할 수 있다. (DI도 지연이 가능하다.)
- final class는 CGLib 기반 프록시를 생성할 수 없다. (final method가 있어서도 안된다.)
- Lookup Method Injection을 사용하면 싱글톤 Bean에서 프로토타입 범위의 Bean을 사용할 수 있다.
- RequestScope Bean을 사용하면 Spring MVC에서 ThreadLocal을 대체할 수 있다.
  - `SpringContext.getBean(T)`에서 리플렉션을 사용하므로 훨씬 느리다. 
  - Context Switching 비용은 줄일 수 있다.
- AOP에서 빈을 프록시로 래핑하는 것은 BeanPostProcessor이다.
- BeanPostPrecssor는 빈 초기화 콜백 메서드가 호출된 후, `InitializingBean.afterPropertiesSet()` 같은 컨테이너 초기화 메서드가 실행되기 전에 콜백을 받게 된다.

> https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## 참고
- https://docs.spring.io/spring-framework/reference
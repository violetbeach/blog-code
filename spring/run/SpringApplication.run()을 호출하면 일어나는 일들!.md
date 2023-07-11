요즘 들어서 Spring의 동작 원리에 대해 많이 부족하다고 느낀다.

2년차인데.. 다시 초심으로 돌아가자 ㅠ

![img_1.png](img_1.png)

보통 스프링부트로 개발하면 `main()` 메서드를 통해 `SpringApplication.run()` 메서드를 호출한다.

`SpringApplication.run()`을 호출하면 일어나는 일에 대해 알아보자.

![img.png](img.png)

## SpringApplication.run()

가장 먼저 알아야할 것은 해당 코드는 `SpringApplication` 클래스는 SpringBoot에서**만** 사용한다는 점이다.

그러면 일반 Spring Legacy 에서는 어떻게 사용할까? Spring Legacy는 SpringApplication을 직접 실행하지 않는다.
빌드를 한 후에 WAS에 `jar`를 내장시켜서 돌리는 방식이다. 

## 코드

![img_8.png](img_8.png)

```java
public ConfigurableApplicationContext run(String... args) {
    // 1. SpringApplication 전체 실행 시간 측정 시작
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    
    // 2. BootstrapContext 생성
    DefaultBootstrapContext bootstrapContext = createBootstrapContext();
    ConfigurableApplicationContext context = null;
    
    // Headless Property 설정 적용
    configureHeadlessProperty();
    
    // 4. Spring Application Listener 실행
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting(bootstrapContext, this.mainApplicationClass);
    try {
    
        // 5. Arguments 및 Environment 생성
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
        
        // 6. IgnoreBeanInfo 설정
        configureIgnoreBeanInfo(environment);
        
        // 7. Spring Banner 출력
        Banner printedBanner = printBanner(environment);
        
        // 8. Application Context 생성
        context = createApplicationContext();
        context.setApplicationStartup(this.applicationStartup);
        
        // 9. ApplicationContext 준비
        prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
        
        // 10. Context Refresh
        refreshContext(context);
        
        // 11. Context Refresh 후처리
        afterRefresh(context, applicationArguments);
        
        // 12. 실행 시간 출력 및 리스너 started 처리
        stopWatch.stop();
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
        }
        listeners.started(context);
        
        // 13. Runners 실행
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, listeners);
        throw new IllegalStateException(ex);
    }

    try {
        listeners.running(context);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, null);
        throw new IllegalStateException(ex);
    }
    return context;
}
```

해당 부분을 잘게 잘라서 살펴보자.

####  1. SpringApplication 전체 실행 시간 측정 시작

```java
StopWatch stopWatch = new StopWatch();
stopWatch.start();
```

SpringApplication을 실행할 때 실행 시간을 측정한다.

#### 2. BootstrapContext 생성

```java
DefaultBootstrapContext bootstrapContext = createBootstrapContext();
```
BootstrapContext는 SpringBoot 애플리케이션 초기 실행 시 사용하는 인터페이스이다.

![img_2.png](img_2.png)

ApplicationContext가 준비되기 전에 **Environment**를 관리하는 역할을 한다.

#### 3. Headless Property 설정 적용

```java
configureHeadlessProperty();
```

`java.awt.headless` 설정을 찾아서 적용합니다. `(default = true)`

해당 값이 true일 경우 Java Application이 window, dialog boxes 등을 표시하지 않으며, 키보드나 마우스를 사용하지 않습니다.

#### 4. Spring Application Listener 실행 

```java
SpringApplicationRunListeners listeners = getRunListeners(args);
listeners.starting(bootstrapContext, this.mainApplicationClass);
```

![img_3.png](img_3.png)

여기서 Listeners는 `SpringApplicationRunListeners`이고, 해당 클래스는 `SpringApplicationRunListener`의 일급 컬렉션이다.

`SpringApplicationRunListener`의 구현체는 아직까지 `EventPublishingRunListener` 밖에 없다.

#### 5. Arguments 및 Environment 생성

```java
ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
```

main() 호출 시 받은 args를 인스턴스화 후, 불러왔던 프로퍼티들을 조합하여 Environment를 생성한다.

Environment의 구조는 대략적으로 아래와 같다.

![img_5.png](img_5.png)

스프링에서는 커맨드 라인 인수, 자바 시스템 속성, OS 환경 변수 등을 추상화하여 Environment를 생성하여 사용한다.

#### 6. IgnoreBeanInfo 설정

```java
configureIgnoreBeanInfo(environment);
```

`spring.beaninfo.ignore` 설정이 존재하지 않는다면 기본값인 true로 설정한다.

해당 옵션은 BeanInfo 클래스 탐색을 생략할 지 여부이다.

#### 7. Spring Banner 출력

```java
Banner printedBanner = printBanner(environment);
```

![img_6.png](img_6.png)

Spring 배너를 출력한다.

#### 8. Application Context 생성

```java
ConfigurableApplicationContext context = createApplicationContext();
context.setApplicationStartup(this.applicationStartup);
```

`Spring Container` 라고도 부르는 `ApplicationContext`를 생성하는 부분이다. 

![img_7.png](img_7.png)

ApplicationContextFactory에게 Context 생성을 위임한다.
ApplicationContextFactory는 아래의 구현체가 있다.
- DefaultApplicationContextFactory
- AnnotationConfigReactiveWebServerApplicationContext.Factory.class
- AnnotationConfigServletWebServerApplicationContext.Factory.class

#### 9. ApplicationContext 준비

```java
prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
```

클린 코드에서 인자는 0~1개가 바람직하다고 했거늘.. 이렇게 많은 코드도 쓰는구나

```java
private void prepareContext(DefaultBootstrapContext bootstrapContext, ConfigurableApplicationContext context,
		ConfigurableEnvironment environment, SpringApplicationRunListeners listeners,
		ApplicationArguments applicationArguments, Banner printedBanner) {
	context.setEnvironment(environment);
	postProcessApplicationContext(context);
	applyInitializers(context);
	listeners.contextPrepared(context);
	bootstrapContext.close(context);
	if (this.logStartupInfo) {
		logStartupInfo(context.getParent() == null);
		logStartupProfileInfo(context);
	}
	// Add boot specific singleton beans
	ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
	beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
	if (printedBanner != null) {
		beanFactory.registerSingleton("springBootBanner", printedBanner);
	}
	if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
		((AbstractAutowireCapableBeanFactory) beanFactory).setAllowCircularReferences(this.allowCircularReferences);
		if (beanFactory instanceof DefaultListableBeanFactory) {
			((DefaultListableBeanFactory) beanFactory)
				.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
		}
	}
	if (this.lazyInitialization) {
		context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
	}
	context.addBeanFactoryPostProcessor(new PropertySourceOrderingBeanFactoryPostProcessor(context));
	// Load the sources
	Set<Object> sources = getAllSources();
	Assert.notEmpty(sources, "Sources must not be empty");
	load(context, sources.toArray(new Object[0]));
	listeners.contextLoaded(context);
}
```

내부적으로는 아래의 동작을 수행한다.

[Context에 주입]
- 생성한 Environment를 주입
- beanNameGenerator 주입
- classLoader, resourceLoader 주입
- ApplicationContextInitializer 주입
- BeanFactoryPostProcessor 주입

[Context를 주입]
- SpringApplicationRunListeners에 Context 주입
- StartupInfoLogger에 Context 주입

[나머지]
- beanFactory에 사용자 설정 적용
  - 순환 참조 허용 (allow_circular_references - default: false)
  - 빈 오버라이딩(allow_bean_definition_overriding - default: false)
- BootStrapContext 종료
- ...

#### 10. Context Refersh 

afterRefresh(context, applicationArguments);

## 정리

SpringApplication.run()이 실행되면 기본적인 스프링 컨테이너 실행과 외부 라이브러리에서 담당하지 않는 부분들을 처리해주는 것 같다. (AWT Header 설정이나 Listener 등록 부분)

## 참고
- https://mangkyu.tistory.com/213
- https://code-run.tistory.com/5
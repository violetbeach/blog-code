어느날 AOP를 활용한 샤딩 라이브러리를 개발하던 중에 버그가 발생했다.

아래는 개발한 라이브러리의 일부이다. 

```java
@Component
@Aspect
public class ShardAspect {
    private final DataSourceFactory dataSourceFactory;

    @Before("@within(org.springframework.transaction.annotation.Transactional)")
    public void setDataSources(JoinPoint joinPoint) throws SQLException {
        // 구현
    }
}
```

문제는 해당 라이브러리가 API 모듈에서는 동작했는데, 컨슈머 모듈에서는 동작하지 않았다.

디버깅을 했는데 생성자 쪽 브레이크가 안걸려서 강제로 의존 주입을 시켜보니 에러가 터졌다. 해당클래스(ShardAspect)가 빈으로 등록되지 않았다.
 
'빈으로 등록이 실패할 수가 있나..? 예외도 안터져..?' 의구심이 들었다. 의존하고 있는 dataSourceFactory는 빈으로 등록이 되어있었다.

## 문제 해결

여러가지 삽질 끝에 문제를 해결했다.

문제는 `@Bean`과 `@Component`의 차이에 있었다.

해당 부분은 모듈에 존재하는 클래스였고, package가 `com.violetbeach.order` 이었다. 내가 개발 중인 코드의 루트 패키지는 `com.violetbeach.shop`이다.

스프링 부트에서 Component를 빈으로 등록하려면 `@ComponentScan`이 필요하다.(`@SpringBootApplication`에 포함된다.)

동작하지 않았던 이유는 `@SpringBootApplication`은 기본적으로 ComponentScan을 해당 루트 패키지를 기준으로 수행하기 때문이다.
- 즉, 위 클래스는 Component Scan 되지 않았으므로 생성을 시도조차 하지 않았고, 예외도 터지지 않았다.
- DataSourceFactory는 `@Configuration`에서 `@Bean`으로 등록되고 있었기 때문에 빈으로 등록이 되어있었다.

```java
@Configuration
public class DataSourceFactoryConfiguration {
	
    @Bean
    public DataSourceFactory dataSourceFactory(final DataSourceCreator creator) {
        return new DataSourceFactory(this.dataSourceMap, creator);
    }
}
```

마지막으로 왜 API 모듈에서는 빈 등록이 성공할 수 있었을까?!

```java
@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.violetbeach")
public class WebConfig implements WebMvcConfigurer {
	
}
```

API 모듈에서는 다른 라이브러리를 통해 `@ComponentScan`을 실행하고 있었다.

### 소감

SpringBoot를 쓰는 데 제대로 사용하지 않다보니 이런 일이 생기는 것 같다.. ㅠ 이런 것 조차 못찾고 삽질을 했다니 부끄럽다.

결과적으로 아래의 Config를 core 모듈에 추가해서 해결할 수 있었다.

```java
@Configuration
@ComponentScan(basePackages = "com.violetbeach")
public class ComponentConfig {

}
```

Configuration + Bean과 Component의 차이를 잘 알고 사용해야 한다. 라이브러리에서는 일반적으로 `@Bean`을 사용하는 것이 훨씬 바람직하다.




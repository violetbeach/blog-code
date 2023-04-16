어느날 Kafka Consumer 모듈을 개발하던 중에 이상한 버그가 발생했다.

아래는 샤딩 처리를 위해 개발한 라이브러리의 일부이다. 

```java
@Component
@Aspect
public class SharedProcessor {
    private final SharedDataSourceManager shardDataSourceManager;

    @Before("@within(org.springframework.transaction.annotation.Transactional) || @annotation(org.springframework.transaction.annotation.Transactional)")
    public void setSharedInfo(JoinPoint joinPoint) throws SQLException {
        String classTransactionManager = "";
        String methodTransactionManager = "";
        Object target = joinPoint.getTarget();
        Class<?> aClass = target.getClass();
        Transactional classAnnotation = (Transactional)aClass.getAnnotation(Transactional.class);
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Transactional methodAnnotation = (Transactional)signature.getMethod().getAnnotation(Transactional.class);
        if (classAnnotation != null) {
            classTransactionManager = classAnnotation.transactionManager().isEmpty() ? classAnnotation.value() : classAnnotation.transactionManager();
        }

        if (methodAnnotation != null) {
            methodTransactionManager = methodAnnotation.transactionManager().isEmpty() ? methodAnnotation.value() : methodAnnotation.transactionManager();
        }

        if ("shardTransactionManager".equals(methodTransactionManager) || "shardTransactionManager".equals(classTransactionManager)) {
            SharedInfo subDbInfo = HiworksThreadLocalStorage.getShardDbInfo();
            if (!this.isValidShardDbInfo(subDbInfo)) {
                throw new SharedInfoException(subDbInfo.getIp(), subDbInfo.getPartitionNo());
            }

            this.shardDataSourceManager.addSharedDataSource(subDbInfo.getIp(), subDbInfo.getPartitionNo());
        }

    }

    private boolean isValidShardDbInfo(SharedInfo subDbInfo) {
        if (subDbInfo.getIp() == null) {
            return false;
        } else if (subDbInfo.getPartitionNo() == null) {
            return false;
        } else if (subDbInfo.getIp().isBlank()) {
            return false;
        } else {
            return !subDbInfo.getPartitionNo().isBlank();
        }
    }

    public SharedProcessor(final SharedDataSourceManager shardDataSourceManager) {
        this.shardDataSourceManager = shardDataSourceManager;
    }
}

```

문제는 해당 라이브러리가 API 모듈에서는 동작했는데, 컨슈머 모듈에서는 동작하지 않았다.

생성자 쪽에 브레이킹 포인트를 찍고 디버그를 실행해봤는데, 브레이크가 안걸려서 강제로 의존 주입을 시켜보니 에러가 터졌다. ShardProcessor가 빈으로 등록되지 않았다.
- 오잉..? 빈으로 등록이 실패할 수가 있나..? 예외도 안터져..?

해당 모듈에서 사용하는 shardDataSourceManager는 빈으로 등록이 되어있었다.

## 문제 해결

여러가지 삽질 끝에 문제를 해결했다. (자책도 했다..)

문제는 Bean과 Component의 차이에 있었다.

해당 부분은 모듈에 존재하는 클래스였고, package가 `com.hiworks.mail` 이었다. 내가 개발 중인 코드의 루트 패키지는 `com.hiworks.office.archive`다.

스프링 부트에서 Component를 빈으로 등록하려면 @ComponentScan이 필요하다.
- @SpringBootApplication에 포함되어 있어서 무뎌져 있었다.

@SpringBootApplication을 사용해도 동작하지 않았던 이유는 @SpringBootApplication은 기본적으로 @ComponentScan을 루트 패키지로 수행하기 때문이다.
- 즉, 위 클래스는 Component지만 Scan되지 않았으므로 생성을 시도조차 하지 않았고, 예외도 터지지 않았다.
- ShardDataSourceManager는 빈으로 등록이 성공한 이유는 해당 클래스는 @Configuration에서 @Bean으로 등록되고 있었기 때문이다.

```java
@Configuration
// 생략
public class ShardDBDatasourceConfiguration {
    
    // 생략

    @Bean
    public ShardDBDataSourceManager subDBDataSourceManager(final ShardDBDataSourceCreator subDBDataSourceCreator, final DataSource subDbDataSource) {
        return new ShardDBDataSourceManager(this.subDBDataSourcesMap, subDBDataSourceCreator, subDbDataSource);
    }
}

```

그러면 마지막으로 왜 API 모듈에서는 빈으로 등록이 성공할 수 있었을까?!

```java
@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.hiworks")
public class WebConfig implements WebMvcConfigurer {

    private final HiworksPagingResolver hiworksPagingResolver;

    private final HiworksFilteringResolver hiworksFilteringResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(hiworksFilteringResolver);
        resolvers.add(hiworksPagingResolver);
    }
}
```

API 모듈에서는 @ComponentScan이 다른 라이브러리를 실행하고 있었다.

### 소감

후.. SpringBoot를 쓰는 데 제대로 사용하지 않다보니 이런 일이 생기는 것 같다.. ㅠ 최근에 Test에서 SpringBootContext 때문에 애먹기도 했었는데.. 이런 것 조차 못찾고 삽질을 했다니 부끄럽다..

결과적으로 아래의 Config를 core 모듈에 추가해서 해결할 수 있었다.

```java
@Configuration
@ComponentScan(basePackages = "com.hiworks")
@ConfigurationPropertiesScan(basePackages = {"com.hiworks.office"})
public class PropertyConfig {

}
```

해결은 가능했지만만, 처음에 봤던 클래스와 같이 모듈에서 Component를 사용한 것은 잘못된 것 같다.
- Configuration + Bean과 Component의 차이를 잘 알고 사용하도록 하자..!
- Spring과 SpringBoot에 대해서도 잘 알고 사용하도록 하자!!





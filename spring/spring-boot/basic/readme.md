## Spring-Boot의 동작원리 이해하기 (+ 자동구성, 라이브러리 잘 사용하기!)

최근에 Spring-Kafka에 기여하게 되고, 사내에서 라이브러리름 많이 만들면서 공통 라이브러리에 대해 관심이 많아졌고, Spring-boot에 대해서 이해도가 많이 부족하다고 느꼈다.

해당 포스팅에서는 SpringBoot의 동작 원리에 대해 간략히 다룬다.

## 기존 방식 vs 스프링 부트

웹 애플리케이션을 개발하고 배포하려면 WAR 방식으로 배포를 했어야 했다.
- 톰캣을 별도로 설치하고 설정을 구성해야 한다.
- 애플리케이션 코드를 WAR로 빌드해야 한다.
- 빌드한 WAR 파일을 WAS에 배포해야 한다.

이러한 방식은 아래의 단점이 있었다.
- WAS를 별도로 설치해야 함
- 개발 환경이 복잡
- 배포 과정도 복잡
- 톰캣의 버전을 변경하려면 톰캣을 다시 설치하고 서버를 다시 구성해야 함

그래서 Spring Mvc 방식 + 내장 톰캣 의존성을 사용하면 이를 일부 해결할 수 있다. (일명 Fat Jar)
- 여전히 코드 및 빌드에서 톰캣을 조작해야 함
- 파일명 중복을 해결할 수 없다.

SpringBoot를 도입하면 이 문제를 해결할 수 있다. 

## @SpringBootApplication

@SpringBootApplication에서는 수 많은 일들이 발생하지만 크게는 2가지 일을 한다.
- 스프링 컨테이너를 생성한다.
- WAS(내장 톰캣)을 생성한다.

## 동작 원리

스프링 부트의 동작 원리를 요약하면 아래와 같다.
1. java -jar xxx.jar
2. MANIFEST.MF 인식
3. JarLauncher.main() 실행
    - BOOT-INF/classes/ 인식
    - BOOT-INF/lib/ 인식
4. BootApplication.main() 실행

#### 1. java -jar xxx.jar
java -jar xxx.jar를 사용해서 jar를 풀면 아래의 파일들이 존재한다.
- xxx.jar
- META-INF
  - MANIFEST.MF
- org/springframework/boot/loader
  - JarLauncher.class : 스프링 부트 main() 실행 클래스
- BOOT-INF
  - classes : 우리가 개발한 class 파일과 리소스 파일
    - hello/boot/BootApplication.class
    - hello/boot/controller/HelloController.class
    - ...
  - lib : 외부 라이브러리
    - spring-webmvc-6.0.4.jar
    - tomcat-embed-core-10.1.5.jar
    - ...
  - classpath.idx : 외부 라이브러리 경로
  - layers.idx : 스프링 부트 구조 경로

#### 2. MANIFEST.MF 인식

아래는 스프링부트가 만든 MANIFEST.MF이다.

```
Manifest-Version: 1.0
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: hello.boot.BootApplication
Spring-Boot-Version: 3.0.2
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Spring-Boot-Layers-Index: BOOT-INF/layers.idx
Build-Jdk-Spec: 17
```

여기서 `Main-Class`는 우리가 만든 main()이 있는 hello.boot.BootApplication이 아닌 `JarLauncher`라는 전혀 다른 클래스를 실행한다. JarLauncher는 스프링부트가 빌드 시 넣어준다.

#### 3. JarLauncher.main() 실행

스프링 부트에서는 Jar 내부에 Jar을 감싸고, 그것을 인식을 가능케 한다. 스프링 부트에서 뭔가 실행을 해서 동작을 시켜야 하는데 그래서 우리가 개발한 앱을 Main을 바로 실행하면 Jar 안에 Jar가 있는 이런 구조를 읽을 수 없다.

그래서 일종의 Hook 역할을 하는 JarLauncher가 XXXMainApplication.main()을 대신 호출한다. 

JarLauncher는 BOOT-INF에 있는 `classes`, `lib`에 있는 jar를 읽은 후 `Start-Class`에 지정된 **main()**을 실행한다.

(IDE를 사용할 때는 JarLauncher를 사용하지 않는다. 실행 가능한 Jar가 아니라 IDE에서 필요한 라이브러리를 모두 인식할 수 있게 도와주기 때문)

## 라이브러리 관리

라이브러리를 관리할 때 각 라이브러리는 호환이 잘되는 라이브러리가 있고, 호환이 잘되는 버전이 있다는 것이다. 더 큰 문제는 호환이 잘 안되는 버전도 있다. 그래서 기존에는 라이브러리의 버전을 선택할 때 많은 시간이 소요되었다.

```groovy
dependencies {
    implementation 'org.springframework:spring-webmvc:6.0.4'
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.5'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.14.1' //스프링 부트 관련
    implementation 'org.springframework.boot:spring-boot:3.0.2'
    implementation 'org.springframework.boot:spring-boot-autoconfigure:3.0.2' //LOG 관련
    implementation 'ch.qos.logback:logback-classic:1.4.5'
    implementation 'org.apache.logging.log4j:log4j-to-slf4j:2.19.0'
    implementation 'org.slf4j:jul-to-slf4j:2.0.6'
    implementation 'org.yaml:snakeyaml:1.33'
}
```

스프링 부트는 개발자가 라이브러리를 편리하게 사용할 수 있게 다양한 기능을 제공한다.
- 외부 라이브러리 버전 관리
- 스프링 부트 스타터를 제공을 해준다.

의존성 관리 플러그인만 설치해주면 스프링 부트 버전에 맞는 라이브러리의 버전으로 관리해준다.

```groovy
plugins {
    id 'org.springframework.boot' version '3.0.7'
    id 'io.spring.dependency-management' version '1.1.0' // 추가
}

dependencies {
  implementation 'org.springframework:spring-webmvc'
  implementation 'org.apache.tomcat.embed:tomcat-embed-core'
  implementation 'com.fasterxml.jackson.core:jackson-databind'
  implementation 'org.springframework.boot:spring-boot'
  implementation 'org.springframework.boot:spring-boot-autoconfigure'
  implementation 'ch.qos.logback:logback-classic'
  implementation 'org.apache.logging.log4j:log4j-to-slf4j'
  implementation 'org.slf4j:jul-to-slf4j'
  implementation 'org.yaml:snakeyaml'
}
```

내부적으로는 Gradle의 의존 관리 플러그인을 사용해서 SpringBoot의 bom 정보를 참고한다. 
- https://jaehoney.tistory.com/345

SpringBoot가 관리하는 외부 라이브러리 버전은 아래를 참고하면 된다. (private 라이브러리나 대중적이지 않은 라이브러리 빼고는 왠만하면 있다.)
- https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html

## 스프링 부트 스타터

버전은 의존성 관리 플러그인으로 편하게 관리가 되지만, 여전히 수 많은 라이브러리가 필요한 문제가 있다.

웹 프로젝트를 하나 사용하려면 위와 같이 많은 라이브러리에 대한 의존성이 필요하다.

```yaml
implementation 'org.springframework.boot:spring-boot-starter-web'
```

이는 내부적으로 여러 라이브러리를 포함하고 있다.

그래서 스프링 부트 스타터 하나만 의존성에 추가해주면 필요한 라이브러리를 전부 가져온다.

## AutoConfiguration

스프링 MVC의 문제 중 하나가 초기에 필요한 빈들을 수동으로 등록해줘야 한다는 것이다.

스프링 부트는 자동 구성(Auto Configuration)이라고 하는 필요한 빈을 자동으로 등록해주는 기능을 제공한다.
- 일반적으로는 spring-boot-starter 안에 있는 spring-boot-autoconfigure 라이브러리가 동작하게 된다.

내부적으로는 아래의 클래스들이 있다.
- JdbcTemplateAutoConfiguration
- DataSourceAutoConfiguration
- JacksonAutoConfiguration
- HttpMessageConvertersAutoConfiguration
- BatchAutoConfiguration
- ...

그 중 JdbcTemplateAutoConfiguration.class을 보자.

```java
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(JdbcProperties.class)
@Import({ DatabaseInitializationDependencyConfigurer.class,
        JdbcTemplateConfiguration.class,
        NamedParameterJdbcTemplateConfiguration.class })
public class JdbcTemplateAutoConfiguration {
}
```

- `@AutoConfiguration`: 자동 구성을 사용하려면 이 애노테이션을 등록해야 한다.
  - 애노테이션 내부에 `@Configuration`이 있어서 빈을 등록하는 자바 설정 파일로 사용할 수 있다.
  - after = `XXXAutoConfiguration.class`
    - 자동 구성이 실행되는 순서를 지정할 수 있다. `JdbcTemplate`은 `DataSource`가 필요하기 때문에 `DataSourceAutoConfiguration.class` 이후에 실행하도록 설정되어 있다.
- @ConditionalOnClass({ DataSource.class, JdbcTemplate.class )
  - 해당 클래스들이 빈으로 등록된 경우에만 빈으로 등록된다.
- @Import
  - 스프링 컨테이너에 자바 설정을 추가할 때 사용한다.

결과적으로 @Import에 포함된 DatabaseInitializationDependencyConfigurer.class, JdbcTemplateConfiguration.class,
NamedParameterJdbcTemplateConfiguration.class가 빈으로 구성된다.

## @Conditional

이 기능은 스프링 부트의 자동 구성 및 라이브러리 등에서 자주 사용된다.
- 특정 조건이 만족하는 지 여부를 구분하는 애노테이션이다.

예를 들어 `@ConditionalOnBean` 애노테이션을 까보면 아래와 같다.

```java
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnBean {
    // 생략
}
```

여기서 `OnBeanCondition.class` 는 `Condition` 인터페이스를 구현하고 있다.

```java
package org.springframework.context.annotation;

public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

해당 인터페이스는 `matchs()` 메서드가 `true`를 반환하면 조건에 만족해서 동작하고, `false`를 반환하면 동작하지 않는다.
- ConditionContext: 스프링 컨테이너, 환경 정보를 담고 있다.
- AnnotatedTypeMetadata: 애노테이션 메타 정보를 담고 있다.

`@Condtiional` 애노테이션이 붙어 있으면 스프링이 로드될 때 `ConditionEvaluator`를 사용해서 Condition 여부를 체크하고 true일 경우 빈으로 등록한다.

사실 다 아는 내용이지만 개발에 필요한 대부분의 `ConditionalOnXXX` 메서드를 스프링이 제공하고 있다.

## 라이브러리

기본적으로 의존성의 경우 컴파일 과정에서 jar 형태로 가져와서 사용한다.

```java
implementation files('libs/module.jar')
```

가령, 위와 같이 특정 모듈의 jar를 프로젝트에 포함할 수 있고, 필요 시 maven과 같은 저장소를 활용할 수도 있다.

라이브러리를 사용할 때 문제는 README.md, GUIDE.md 등에 사용 방법을 명확하게 명시해줘야 한다.
- ...Config를 컴포넌트로 스캔해줘야 하고
- ...빈을 등록해줘야 하고
- ...

이런 처리를 모두 사용측에서 하기는 귀찮다. 그래서 그냥 라이브러리에서 대부분의 처리를 대신 해줄 수 있는 방법이 있다. 위에서 언급한 스프링 부트 **자동 구성(AutoConfiguration)**이다.

### AutoConfiguration

라이브러리를 제공하는 사람이 코드 1줄을 쓰면 가져다 쓰는 사람 100만명의 1줄을 아낄 수 있다. 

프로젝트에 라이브러리를 추가하기만 하면 모든 구성이 자동으로 처리되도록 구성해보자.

```java
// @Configuration 삭제
@AutoConfiguration
@ConditionalOnProperty(name = "memory", havingValue = "on")
public class MemoryAutoConfig {
    
    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }
    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
    
}
```

`@Configuration`을 `@AutoConfiguration`으로 교체했다. 그리고 환경에 따라 해당 기능을 켜고 끌 수 있게 하기 위해서 `ConditionalOnProperty`를 추가했다.

### 자동 구성 대상 지정

이대로 프로젝트를 빌드해도 해당 @AutoConfiguration이 등록되지 않는다. 대상을 지정해야 한다.

아래 파일을 생성한다.

```
resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

해당 파일에는 아래의 내용을 추가한다.
```
memory.MemoryAutoConfig
```

이제 해당 모듈을 Import하면 해당 파일을 읽어서 AutoConfiguration이 자동으로 등록된다.

### spring.factories

자동 구성을 구성하는 두 가지 주요 방법 중 `spring.factories`를 활용하는 방법도 있다.

해당 파일을 활용하면 `@Configuration`을 그대로 사용할 수 있다.

`resources/META-INF/spring.factories`에

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  memory.MemoryAutoConfiguration
```

그러면 앱이 실행될 때 EnableAutoConfiguration 목록에 MemoryAutoConfiguration이 추가된다.

### 차이

일반적으로 외부 라이브러리에서 제공하는 구성을 사용하거나, 애플리케이션에서 커스텀한 구성을 정의할 때 `spring.factories`를 사용한다.

그리고 내부 라이브러리에서 제공하는 구성을 확장하거나, 특정 모듈의 자동 구성을 정의할 때는 `org.springframework.boot.autoconfigure.AutoConfiguration.imports` 를 주로 사용한다고 한다.

추가로 spring.factories의 경우 Configuration을 등록하는 것 이외에도 아래와 같이 다양한 것을 할 수 있다.

```java
# ApplicationContext Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer

# Environment Post Processors
org.springframework.boot.env.EnvironmentPostProcessor=\
org.springframework.boot.autoconfigure.integration.IntegrationPropertiesEnvironmentPostProcessor

# Auto Configuration Import Listeners
org.springframework.boot.autoconfigure.AutoConfigurationImportListener=\
org.springframework.boot.autoconfigure.condition.ConditionEvaluationReportAutoConfigurationImportListener

# Auto Configuration Import Filters
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
org.springframework.boot.autoconfigure.condition.OnClassCondition,\
org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition

# Failure Analyzers
org.springframework.boot.diagnostics.FailureAnalyzer=\
org.springframework.boot.autoconfigure.data.redis.RedisUrlSyntaxFailureAnalyzer,\
org.springframework.boot.autoconfigure.diagnostics.analyzer.NoSuchBeanDefinitionFailureAnalyzer,\
org.springframework.boot.autoconfigure.flyway.FlywayMigrationScriptMissingFailureAnalyzer,\
org.springframework.boot.autoconfigure.jdbc.DataSourceBeanCreationFailureAnalyzer,\
org.springframework.boot.autoconfigure.jdbc.HikariDriverConfigurationFailureAnalyzer,\
org.springframework.boot.autoconfigure.jooq.NoDslContextBeanFailureAnalyzer,\
org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBeanCreationFailureAnalyzer,\
org.springframework.boot.autoconfigure.r2dbc.MissingR2dbcPoolDependencyFailureAnalyzer,\
org.springframework.boot.autoconfigure.r2dbc.MultipleConnectionPoolConfigurationsFailureAnalyzer,\
org.springframework.boot.autoconfigure.r2dbc.NoConnectionFactoryBeanFailureAnalyzer

# Template Availability Providers
org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider=\
org.springframework.boot.autoconfigure.freemarker.FreeMarkerTemplateAvailabilityProvider,\
org.springframework.boot.autoconfigure.mustache.MustacheTemplateAvailabilityProvider,\
org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAvailabilityProvider,\
org.springframework.boot.autoconfigure.thymeleaf.ThymeleafTemplateAvailabilityProvider,\
org.springframework.boot.autoconfigure.web.servlet.JspTemplateAvailabilityProvider

# DataSource Initializer Detectors
org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector=\
org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializerDatabaseInitializerDetector

# Depends on Database Initialization Detectors
org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector=\
org.springframework.boot.autoconfigure.batch.JobRepositoryDependsOnDatabaseInitializationDetector,\
org.springframework.boot.autoconfigure.quartz.SchedulerDependsOnDatabaseInitializationDetector,\
org.springframework.boot.autoconfigure.session.JdbcIndexedSessionRepositoryDependsOnDatabaseInitializationDetector
```

그래서 SpringBoot 프로젝트의 내부를 보면 AutoConfiguration의 경우 `org.springframework.boot.autoconfigure.AutoConfiguration.imports`로 등록하고, Provider 등과 같은 경우 `spring.factories`를 활용하고 있다.

- https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
- https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/resources/META-INF/spring.factories

## 참고
- 김영한님 스프링부트 핵심 원리와 활용: https://inf.run/LXBX
- https://javacan.tistory.com/entry/spring-boot-auto-configuration
## Spring-Boot의 동작 원리 이해하기!

최근에 Spring-kafka와 같은 외부 라이브러리에 대해 관심이 많아졌고, 코드를 분석하는 과정에서 Spring-boot에 대해서 이해도가 많이 부족하다고 느꼈다.

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

```java
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

스프링 부트에서는 Jar 내부에 Jar을 감싸고, 그것을 인식을 가능케 한다. 그래서 Jar 안에 있는 우리가 만든 Jar 파일을 JarLauncher가 읽어온 다. 

JarLauncher는 BOOT-INF에 있는 우리가 개발한 클래스와 리소스, 외부 라이브러리, 구조 정보 등을 읽어 들이고 `Start-Class`에 지정된 **main()**을 실행한다.

(IDE를 사용할 때는 JarLauncher를 사용하지 않는다. 실행 가능한 Jar가 아니라 IDE에서 필요한 라이브러리를 모두 인식할 수 있게 도와주기 때문)

## spring.factories

## 참고
- 김영한님 스프링부트 핵심 원리와 활용: https://inf.run/LXBX
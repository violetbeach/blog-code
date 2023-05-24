## Gradle - DependencyManagement 이해하기!

멀티 모듈 프로젝트에서 외부 라이브러리를 사용할 때 이런 고민이 있다.
- A 프로젝트와 B 프로젝트와 C 프로젝트에서 모두 동일한 버전의 라이브러리를 사용하고 싶다.
- 각 프로젝트의 build.gradle에서 라이브러리 의존성 버전을 동기화해줘야 한다.

이러한 문제를 더 잘 해결할 수 없을까..?

## Dependency-management-plugin

Gradle의 의존성 관리 플러그인(Dependency-management-plugin)을 사용하면 이를 해결할 수 있다.

의존성 관리 플러그인은 Maven과 같은 의속성의 관리 및 제외를 제공하는 Gradle Plugin이다.
- Gradle 6.8 이상, Java 8 버전 이상에서 지원된다.

## 사용하기 전에

DependencyManagement는 Gradle 플러그인 포탈에서 가져와 적용할 수 있다.
- https://plugins.gradle.org/plugin/io.spring.dependency-management

![img_3.png](img_3.png)

![img_4.png](img_4.png)

## 사용 방법

## 실전 코드

아래는 root_gradle의 일부이다.

![img_1.png](img_1.png)

저렇게 dependencyManagement를 등록하면 관련 모듈들의 버전이 일괄적으로 관리된다.

![img_2.png](img_2.png)

그래서 하위 모듈에서는 버전을 명시하지 않고 의존성을 추가해주기만 하면 된다.
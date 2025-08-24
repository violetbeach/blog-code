## 커넥션

HikariCP를 사용하면 커넥션 풀을 사용할 때보다 응답 시간이 100배 이상 빠르다.

## ConnectionProvider

아래는 ConnectionProvider를 구현하는 4개의 구현체이다.

## DriverManagerConnectionProvider

`DriverManagerConnectionProvider`는 프로덕션 환경에서 사용하는 것이 권장되지 않는다.

## HikariConnectionProvider

HikariConnectionProvider는 HikariCP 라이브러리가 내장되어 있다.

HikariCP는 JPA나 Hibernate 커넥션 프로퍼티를 읽지 않는다. 따라서 Hibernate 프로퍼티를 설정해야 한다.
- `hibernate.hikari.datasourceClassName`
- ...

## DataSourceConnectionProvider

아펏 설명한 ConnectionProvider가 JDBC 드라이버 기반으로만 동작하는 RESOURCE_LOCAL 트랜잭션 타입만 지원한다. 즉, 트랜잭션을 직접 설정하고 관리하기가 어렵다.

DataSourceConnectionProvider는 JTA Transaction Manager 방식도 지원한다. 설정된 DataSource를 기반으로 동작하기에 훨씬 유연한 관리가 가능하다.

## Lifecycle

Hibernate 5.2부터는 `hibernate.connection.handling_mode`로 연결/해제 전략을 설정할 수 있다.
- RESOURCE_LOCAL: DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION
- JTA: DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT

리소스 로컬 트랜잭션은 트랜잭션이 끝날 때 연결을 해제하지만, JTA 트랜잭션은 Statement가 실행된 후에 연결이 해제된다.

Hibernate 5.2.10부터는 RESOURCE_LOCAL 트랜잭션을 사용할 때 Connection을 얻기 위해 Jdbc Connection의 getAutoCommit()을 먼저 호출해야 하는데, 이 부분이 해소되었다. `hibernate.connection.provider_disables_autocommit` 프로퍼티를 true로 설정하면 된다.

해당 속성을 사용하면 Connection을 Lazy로 획득하기에 ConnectionLeaseTime이 P95 기준으로 90ms -> 14ms 정도로 감소하는 효과가 있다.

## Connecteion Monitoring

- Connection Pool size
- Queuing Theory and and Little's Law
- FlexyPool

적절한 풀 사이즈를 찾으려면 연결 사용량을 모니터링하고, 커넥션 획득 시간과 해제까지의 유지 시간을 분석해야 한다. 그리고 커넥션 획득에서 타임아웃이 발생한다면 자동으로 풀 크기를 조정할 수 있어야 한다.







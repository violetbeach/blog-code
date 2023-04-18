## HikariCP 커넥션 누수 해결기 (Session wait_timeout 적용!)

아래의 에러가 뜨면서 커넥션 연결이 실패하는 이슈가 발생했다.

```java
the last packet successfully received from the server was 30,035 milliseconds ago. The last packet  sent successfully to the server was 30,036 milliseconds ago.
```

조금더 로그를 올라가보니 이런 경고가 뜨고있었다.

```java
023-04-18 08:01:12.345  WARN 20 --- [nio-8080-exec-9] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@1d5d809b (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.346  WARN 20 --- [nio-8080-exec-2] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@656228eb (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.346  WARN 20 --- [nio-8080-exec-2] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@1ee6e7a8 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.347  WARN 20 --- [nio-8080-exec-9] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@100d934e (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.347  WARN 20 --- [nio-8080-exec-2] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@316d65af (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.347  WARN 20 --- [nio-8080-exec-9] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@5a179137 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.348  WARN 20 --- [nio-8080-exec-2] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@4cf3ac6d (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.348  WARN 20 --- [nio-8080-exec-9] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@2bbf9bb6 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.348  WARN 20 --- [nio-8080-exec-2] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@38a2e068 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:01:12.349  WARN 20 --- [nio-8080-exec-9] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@1ed2242a (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
```

시간이 지나면 아래와 같이 모든 커넥션으로 연결을 시도하다가 모두 실패했다.
```java
2023-04-18 08:19:35.589  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@60422b6c (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value.
2023-04-18 08:19:35.590  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@2e035a5c (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.590  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@7b47c35 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.591  WARN 20 --- [nio-8080-exec-8] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@24a31414 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.591  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@76dc3ee4 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.592  WARN 20 --- [nio-8080-exec-8] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@362af980 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.592  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@4a75497 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.592  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@30422fe5 (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
2023-04-18 08:19:35.593  WARN 20 --- [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase          : HikariPool-3 - Failed to validate connection com.mysql.cj.jdbc.ConnectionImpl@1433b80c (No operations allowed after connection closed.). Possibly consider using a shorter maxLifetime value. 
```

## 원인

원인은 사실 대부분 잘 알다시피 커넥션 누수다.

App단의 HikariCP의 maxTimeOut으로 인해 커넥션 연결을 끊기도 전에 MySQL에서 wait_timeout으로 인해 연결을 끊어버리면 위와 같이 커넥션 누수가 발생한다.
- HikariCP와 Tomcat-dbcp 철학이 달라서 발생한 문제라고 한다.
  - HikariCP는 Tomcat-dbcp와 달리 사용하지 않는 Connection을 빠르게 회수하도록 설계
  - Tomcat-dbcp는 지속적으로 DB에 Validation Query를 보내서 커넥션이 끊어지지 않도록 설계


## 해결 방법 (잘 알려진 솔루션)

### 1. maxTimeOut 조정

HikariCP의 maxTimeOut을 DB의 wait_timeout보다 2~3초 낮게 설정하는 것을 권장한다는 가이드가 많다.

문제는 현재 MySQL 운영 서버의 wait_timeout이 15초이고, HikariCP의 maxTimeOut의 최솟값은 30초이다.

```java
private void validateNumerics(){
    if(maxLifetime!=0&&maxLifetime<SECONDS.toMillis(30)){
            LOGGER.warn("{} - maxLifetime is less than 30000ms, setting to default {}ms.",poolName,MAX_LIFETIME);
            maxLifetime=MAX_LIFETIME;
    }
    // ... 생략
}
```

HikariCP는 maxTimeOut을 30초보다 낮게 잡으면 Default인 30분으로 강제로 설정한다 ㅠ
- HikariCP에서 maxTimeOut이 너무 낮으면 성능 저하나 커넥션 끊기는 이슈가 발생을 우려한 부분이다.

결국 해당 부분은 라이브러리를 고쳐야 했는데 권장되지 않는 방법이라서 적용하지 않았다.

### 2. wait_timeout 조정

MySQL 서버의 wait_timeout을 조금 더 높게 수정을 하면 이를 해결할 수 있다고 한다.

하지만 개발 운영팀에서는 기존의 PHP 레거시 코드 때문에 wait_timeout을 높이지 못한다는 답변을 받았다.
- 기존에 존재하는 PHP 코드는 스크립트 언어라서 커넥션을 맺고 커넥션을 닫지 않고 종료되는 일이 너무 많다.
- 그 경우에 빨리 커넥션을 끊지 않으면 성능이 크게 저하되다가 서버가 터질 수 있다고 한다.

### 3. autoreconnect 옵션을 설정

HikariCP의 JdbcUrl에 autoreconnect 파라미터를 true로 설정할 수 있다.

단, 해당 부분은 트랜잭션이 진행중일 때 연결이 끊기면 즉시 커밋되는 이슈가 있어서 정합성에 문제가 있었다.

결국 이 방법도 적용할 수 없었다.

## 적용한 방법

다음으로 선택한 방법이 세션의 wait_timeout을 설정하는 방법이다.

wait_timeout 설정도 GLOBAL 설정이 있고, SESSION 설정이 있었다.

즉, SpringBoot 앱에서 MySQL 서버로 연결할 때 세션의 wait_timeout을 바꿀 수 있다면 해결할 수 있지 않을까..? 하는 생각이었다.

```java
hikari:
      pool-name: SpringBootJPAHikariCP
      maximum-pool-size: 10
      connection-timeout: 10000
      validation-timeout: 10000
      max-lifetime: 580000
      connection-init-sql: set wait_timeout = 600
```



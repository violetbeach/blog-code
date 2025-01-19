![img.png](img.png)

내가 속한 팀원이 사내 기술 블로그에 로컬 캐시 관련 포스팅을 했고, 기술 리뷰어로 나를 지정했다.

해당 팀원의 성장과 내용의 질을 위해 캐시와 관련된 학습을 하게 되었다.

## 1. 로컬 캐시 vs 글로벌 캐시

일반적으로 캐시는 조회에서의 **성능 향상**과 **부하 방지**를 위해 사용한다. 다수의 요청에서 연산을 위한 리소스나 DB 조회를 할 때 드는 비용 등을 줄일 수 있다. 

**로컬 캐시**는 각 서버의 메모리에 데이터를 저장하는 방식을 말한다.
- 애플리케이션에서 `Map`과 같은 자료구조에 저장하는 경우
- EhCache 라이브러리를 사용하는 경우
- ...

로컬 캐시의 특징은 아래와 같다.

- 속도가 매우 빠르다.
- 외부 인프라와 통신이 필요없다.
  - 외부 통신의 리스크도 줄어든다.
- 각 서버간 다른 데이터가 저장될 수 있다.
  - 동기화가 어렵다.
  - 데이터가 중복해서 저장될 수 있다.
  
**글로벌 캐시**는 캐시 서버를 따로 분리하여 캐시 결과를 저장한다.
- Redis, Memcached 등 외부 인프라에 데이터를 저장하는 경우

글로벌 캐시의 특징은 아래와 같다.
- 네트워크 통신을 하므로 속도가 로컬 캐시보다는 느리다. (일반적인 DB 보다는 훨씬 빠르다.)
- 각 서버에서 동일한 데이터를 조회할 수 있다.
- 전역적으로 데이터를 저장하므로 공간 효율이 비교적 좋다.

로컬 캐시와 글로벌 캐시는 용도에 맞게 사용하는 것이 중요하다.

#### 예시

간단하게 유튜브 서비스를 예로 들어보자.

국가의 목록을 조회할 때는 **로컬 캐시**를 사용할 수 있다.
- 개수가 한정적이다. (공간 복잡도를 그렇게 많이 차지하지는 않는다.)
- 동기화가 반드시 필요하지는 않다.

각 게시글의 좋아요 수를 보여주기 위해서는 **글로벌 캐시**를 사용할 수 있다.
- 각 로컬 캐시에 모두 보관하기에는 공간이 부족하다.
- 동기화가 필요하다. (새로고침을 할 때마다 좋아요 수가 달라지면 안된다.) 

## 2. 로컬 캐시 + 글로벌 캐시

한 서비스에서 특정 상황에서는 로컬 캐시가 적합하고, 다른 상황에서는 글로벌 캐시가 적합하다면 어떻게 하면 될까?

둘다 사용하면 된다.

Spring을 예로 들면 아래 기술들을 사용하면 로컬 캐시 + 글로벌 캐시를 사용할 수 있다.
1. spring-cache
2. ehcache
3. spring-data-redis

Spring에서는 spring-cache 프레임워크를 사용해서 캐시 데이터를 관리할 수 있다. 각 캐시 구현 기술을 구현하는 CacheManager를 빈으로 등록한다.

```java
// Redis cache manager 빈 등록
@Bean(name = "redisCacheManager")
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
  return RedisCacheManager.builder(connectionFactory).build();
}

// EhCache manager 빈 등록
@Bean(name = "ehCacheManager")
public CacheManager cacheManager() {
  return new EhCacheManager();
}
```

그리고 spring-cache에서 지원하는 애노테이션의 `cacheManager` 속성에 사용할 CacheManager의 빈 이름을 명시해주면 된다.

```java
@Cacheable(key = "'user:'+#id", cacheManager = "redisCacheManager")
public User find(Integer id){
  return new User(id);
}
```

## 참고

- https://velog.io/@bonjugi/Cacheable-EhCache와-RedisCache-둘다-사용하기-CacheManager
- https://kk-programming.tistory.com/83

## 참고

- https://tech.kakaopay.com/post/local-caching-in-distributed-systems

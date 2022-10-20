## 대규모 서비스 - 샤딩을 처리하는 다양한 기법들!

이전에 Java Spring에서 샤딩을 처리하지 못하던 이슈를 해결한 경험이 있다. 
- 해결 과정: https://jaehoney.tistory.com/180

그때 당시는 해당 기술이 샤딩(Sharding)인지도 몰랐다.

추가로 당시는 대단한 일을 한 것처럼 느껴졌지만 지금은 샤딩을 처리하는 다양한 기술들을 알게 되었다.
- Naver D2, 우아한형제들 기술블로, 카카오 뱅크 발표, ...

그래서 해당 기술들을 공부하고, 내가 이전에 구현한 코드를 어떻게 개선할 수 있을 지 생각해보기 위해 포스팅을 하게 되었다.

# 샤딩을 처리하는 기법

DB 서버 1대로 트래픽이나 저장량이 감당이 안될 때, Local cache나 Global cache를 동원하기도 한다. 그리고 Master-slave 패턴을 도입해서 읽기 작업의 경우 여러 대의 Slave로 구성하기도 한다.

하지만, 총 저장 용량이 많고 부하가 클 때는 여러 개의 DB서버로 분리해야 하고 이를 샤딩 (Sharding)이라고 한다.

샤딩은 크게 아래의 솔루션으로 나눌 수 있다.
- 앱 서버에서 처리하는 방법
- DB 단위에서 처리하는 방법
- 플랫폼(추가 인프라) 단위로 처리하는 방법

## DB 스토리지 엔진 단위에서 처리하는 방법

대표적으로 Spider, nStore, MongoDB 등의 기술이 있다.

Spider를 예로 들면 MySQL의 스토리지 엔진 중 하나이다.

Spider를 사용하면 디비 서버의 환경 또는 구조를 변경하지 않고도 샤딩이 가능하다.

Spider는 MySQL의 파티셔닝 기능을 사용해서 각 파티션마다 다른 DB서버로 데이터를 저장할 수 있게 하는 구조이다.

다른 DB 서버와 분산 트랜잭션, 조인 등도 할 수 있다는 장점이 있다.

단점은 아래의 단점들이 있다.
- SPIDER 테이블을 드롭해도 데이터 노드의 데이터는 사라지지 않는다. (링크만 제거 된다.)
- TRUNCATE 명령문을 사용하면 모든 MySQL Server의 데이터를 지워버린다.
- 파티션을 전제로 한다.
- 전체 텍스트 검색과 R-Tree 인덱스를 지원하지 않는다.

> 참고: http://file.pmang.kr/images/noc/img/pdf/noc_tr2_se9_choi.pdf

## 플랫폼 단위에서 처리하는 방법

이전에 샤딩은 애플리케이션 서버 레벨에서 구현하는 경우가 많았다. 최근에는 이를 플랫폼 차원에서 제공하려는 시도가 늘고 있다.

별도 인프라를 통해 Middle tier에서 샤딩을 해결할 수 있다면 DB나 앱 서버에서 해당 책임을 분리할 수 있으므로 큰 장점이 있다..!

대표적으로는 Spock proxy, Gizzard, Cubrid shard등이 있다.

Naver에서는 이러한 요구사항을 위해 Nbase-T라는 저장 플랫폼을 사용하고 있다.


## MySQL의 한계

MySQL에서 flags(bigint) 필드만 가지고 비트 연산 쿼리를 수행하는 경우를 생각해보자.

MySQL의 flags (bigint) 필드만 가지고는 비트 연산 쿼리에서 인덱스를 활용할 수 없다.

내부적으로는 특정 범위 내의 한정된 개수를 정해진 순서로 비교해 그 값이 참인 경우 결과를 반환하는 수 밖에 없게 된다.

그러므로 범위가 큰 데이터를 대상으로 비트 비교 연산 쿼리를 수행하기보다는, 정해진 사용자의 데이터 내, 특정할 필드 순서, 제한된 개수만큼을 선택해야 빠르게 결과를 가져올 수 있다.

즉, 찾고자 하는 조건이 데이터 세트의 처음과 마지막에 흩어져 있는 경우에는 해당 범위 내 전체 데이터를 모두 비교하게 될 것이다. -> O(n)에서 적절한 범위 제한을 통해 n을 줄이는 방법 밖에 없다.

이러한 한계 때문에 MySQL만으로는 빠른 응답 속도를 보장할 수 없어서 캐시를 도입해야 한다.

**Arcus**

MySQL이 제공하는 비트 비교 연산을 제공하는 메모리 기반의 캐시는 현재 없다. 심지어 다양한 자료구조를 제공하는 Redis에도 해당 기능은 없다. 하지만 Naver에서 확장 개발한 Memcached cluster인 Arcus를 사용하면 관련 기능을 쓸 수 있게 된다.

## 참고
- https://d2.naver.com/news/3435170
- https://d2.naver.com/helloworld/14822
- https://tech.kakao.com/2016/07/01/adt-mysql-shard-rebalancing/
- https://d2.naver.com/helloworld/5812258
- https://d2.naver.com/helloworld/6070967
- MySQL Fabric
- 샤딩 스피어
- Gizzard, Spock Proxy
- HibernateORM
- https://tech.kakao.com/2016/07/01/adt-mysql-shard-rebalancing/
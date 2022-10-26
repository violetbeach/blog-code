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

## DB 서버에서 자체적으로 처리하는 방법

대표적으로 Spider, nStore, MongoDB, MySQL Fabric 등의 기술이 있다.

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

## 앱 서버 단위에서 처리하는 방법

DB 서버에서 자체적으로 처리하면 DBMS에 종속적이라는 단점이 있다.

추가로, 기존의 DB 서버를 마이그레이션 하는 데에도 큰 비용이 소요된다.
- 이때 앱 서버에서 처리하는 방법을 고려할 수 있었다.

방법으로는 대표적으로 Hibernate Shards, ShardingSphere 등이 있다.

이 중 샤딩 스피어는 아파치에서 제공하는 오픈 소스로 현재에도 Fork 수 6.1k, Starred 17.5k로 검증된 라이브러리라고 볼 수 있다.

샤딩 스피어를 사용하면 앱 서버에서 샤딩 전략(Strategy)를 만들면 해당 전략에 따라 DB 서버를 매핑해서 처리해준다.
- 높은 효과에 비해 사용이 쉬운 편이다.

아래 레퍼런스를 보면 카카오 뱅크 다니시는 분의 샤딩 스피어를 경험한 이야기가 자세히 적혀있다!
> 참고: https://www.sosconhistory.net/soscon2019/content/data/session/Day%202_1430_2.pdf

## 플랫폼 단위에서 처리하는 방법

이전에 샤딩은 애플리케이션 서버 레벨에서 구현하는 경우가 많았다. 최근에는 이를 플랫폼 차원에서 제공하려는 시도가 늘고 있다.

별도 인프라를 통해 Middle tier에서 샤딩을 해결할 수 있다면 DB나 앱 서버에서 해당 책임을 분리할 수 있으므로 큰 장점이 있다..!

대표적으로는 Spock proxy(MySQL Proxy 기반), Gizzard(Twitter에서 만든 분산 관리 라이브러리), Cubrid shard등이 있다.

앱 서버 단에서 모든 질의는 **DB 서버**가 아닌 해당 플랫폼을 통해 이루어진다.

**Gizzard**

Gizzard는 네트워크 서비스 미들웨어로 네트워크를 통해 데이터 저장소에 복제하도록 디자인되어있다. 그래서 데이터 저장소를 MySQL, SQL Server, Redis 등 어떠한 솔루션을 선택해도 큰 문제가 없다.

Twitter에서 만든 Gizzard의 경우 특별한 인스턴스 형태는 없는 구조이고, JVM 위에서 실행되며 상당히 효율적이다.

> 참고: https://gywn.net/2012/03/gizzard-a-library-for-creating-distributed-datastores/

**Naver**

Naver에서는 이러한 용도를 위해 자체 개발한 Nbase-T라는 저장 플랫폼을 사용하고 있다고 한다.

Nbase는 무중단으로 샤드 데이터베이스를 추가(Scale Out)하면서 트래픽 증가에 대응할 수 있다.

Nbase-T는 RDBMS를 기반으로 하고 마이그레이션 및 실시간 데이터 동기화가 용이하다.

> 참고: https://deview.kr/data/deview/session/attach/1500_T2_%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A7%E1%86%BC%E1%84%92%E1%85%A7%E1%86%AB_%E1%84%82%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%87%E1%85%A5%20%E1%84%91%E1%85%A6%E1%84%8B%E1%85%B5%20%E1%84%87%E1%85%A2%E1%84%89%E1%85%A9%E1%86%BC%20EDA%20%E1%84%8C%E1%85%A5%E1%86%AB%E1%84%92%E1%85%AA%E1%86%AB%E1%84%80%E1%85%B5.pdf

## MySQL의 한계

MySQL에서 flags(bigint) 필드만 가지고 비트 연산 쿼리를 수행하는 경우를 생각해보자.

MySQL의 flags (bigint) 필드만 가지고는 비트 연산 쿼리에서 인덱스를 활용할 수 없다.

내부적으로는 특정 범위 내의 한정된 개수를 정해진 순서로 비교해 그 값이 참인 경우 결과를 반환하는 수 밖에 없게 된다.

그러므로 범위가 큰 데이터를 대상으로 비트 비교 연산 쿼리를 수행하기보다는, 정해진 사용자의 데이터 내, 특정할 필드 순서, 제한된 개수만큼을 선택해야 빠르게 결과를 가져올 수 있다.

즉, 찾고자 하는 조건이 데이터 세트의 처음과 마지막에 흩어져 있는 경우에는 해당 범위 내 전체 데이터를 모두 비교하게 될 것이다. -> O(n)에서 적절한 범위 제한을 통해 n을 줄이는 방법 밖에 없다.

이러한 한계 때문에 MySQL만으로는 빠른 응답 속도를 보장할 수 없어서 캐시를 도입해야 한다.

**Arcus**

MySQL이 제공하는 비트 비교 연산을 제공하는 메모리 기반의 캐시는 현재 없다. 심지어 다양한 자료구조를 제공하는 Redis에도 해당 기능은 없다. 하지만 Naver에서 확장 개발한 Memcached cluster인 Arcus를 사용하면 관련 기능을 쓸 수 있게 된다.

## In-memory

추가로 지속적으로 늘어나는 사용자를 MySQL 샤딩으로만 감당하기에 버거운 경지에 이르면 어떻게 될까..?!

네이버 d2를 참고한 결과 MySQL을 대체할 데이터베이스로 Redis를 많이 선택하는 것 같다.

아래의 레퍼런스 처럼 Redis와 Zookeeper 등을 조합해서 Redis cluster를 구성하기도 하고

> 참고: https://d2.naver.com/helloworld/294797
 
nBase-ARC라는 네이버에서 자체적으로 개발한 Redis-Cluster 플랫폼을 사용하는 것 같다. (꼭 한번 보시길 권해드린다!)
- https://d2.naver.com/helloworld/614607

## 참고
- https://d2.naver.com/news/3435170
- https://d2.naver.com/helloworld/14822
- https://tech.kakao.com/2016/07/01/adt-mysql-shard-rebalancing/
- https://d2.naver.com/helloworld/5812258
- https://d2.naver.com/helloworld/6070967
- https://tech.kakao.com/2016/07/01/adt-mysql-shard-rebalancing/
- https://d2.naver.com/helloworld/915127
- https://engineering.linecorp.com/ko/blog/line-manga-server-side/
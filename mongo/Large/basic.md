## MongoDB는 대용량 데이터를 처리하는 Database일까?

## 대용량 데이터

대용량 데이터의 기준은 어떻게 될까?

회사마다 기준이 다르겠지만, 대용량 데이터의 기준은 1PB라고 주로 얘기한다.

## MongoDB는 대용량 데이터를 위한 DB일까?

그렇다면 MongoDB는 1PB의 데이터를 감당할 수 있을까..?

MongoDB Atlas에서 어느정도 스펙까지 서버를 제공해주는 지를 보면 객관적인 수치라고 볼 수 있을 것이다.

MongoDB Atlas에서 제공해주는 크기(조율이 가능) 중 가장 높은 스펙의 경우 RAM = 768GB, Storage(SSD) = 4096GB 이다. 즉 Storage는 4TB이고 기본 운영에도 스토리지가 사용된다는 것을 감안하면 300대가 있으면 1PB를 커버할 수 있다.

1PB = 4TB (Sharded Cluster) * 300 정도

참고로 4TB 클러스터 300대면 MongoDB Atlas 기준 1년에 1200억원 정도의 비용이 발생한다.

결론적으로 1PB를 운영할 수는 있다.

### 실제 관리 측면

실제로 관리하는 측면에서는 MongoDB가 대용량 데이터베이스를 처리하는 데이터는 아닐 수 있다.

MongoDB는 1PB 정도의 환경이 되면 MongoDB답게 사용할 수 없는 지경에 이른다.
- 복제, 복구 데이터 밸런싱에 추가 작업 발생
- 확장시 밸런싱으로 인한 부하 발생
- Index 생성 시 오래 걸려서 실패가 빈번하게 발생
  - 각 Shard에서 Rolling으로 생성해야 함 
- 데이터가 쌓일 수록 속도가 저하된다.
  - 아카이빙이 필요하게 됨 (오래된 데이터는 따로 보관하고 실제로 사용할 데이터만 보관)

그래서 1PB를 기준으로 본다면 MongoDB는 대용량 데이터가 아닐 수 있다.

그것보다는 고성능으로 OLTP 성 데이터를 다루는 서비스에 가깝다. (아래에서 설명한다.)

단, 100TB 이하정도라면 MongoDB에서도 충분히 관리가 가능하다고 한다.

## OLTP

OLTP(Online Transactional Processing)의 경우 아래의 특징을 가진 서비스이다.
- 대량의 Transaction을 처리
- 간단한 쿼리
- CRUD가 다양하게 사용
- 빠른 응답 속도 필요
- Business Operations

Mongo의 경우 OLTP에 적합하다고 말할 수 있다.

```
은행이나 증권과 같이 데이터의 안전성과 일관성이 중요한 경우에는 다수의 사용자에 의해 트랜잭션이 많이 발생하더라도

Mongo처럼 NoSQL 보다는 RDBMS가 더 적합할 수 있다.

그러나 최근에는 NoSQL도 ACID를 준수하는 방식으로 개발되고 있어서, NoSQL을 사용하는 금융 기관도 늘고 있다.
```

## OLAP

OLAP(Onnline Analytical Processing)의 경우 OLTP와는 정반대의 성격을 가진다.
- 대량의 데이터를 처리
- 복잡한 쿼리
- SELECT(Aggregation) 기반
- 응답 속도보다는 많은 량의 데이터에 집중
- Business Decisioning

OLAP의 대표적인 예로는 Wide Column Database가 있다.
- HBase, Cassandra

- Wide Column Database는 대용량 파일 시스템 기반으로 동작
  - HBase의 경우 Hadoop 위에서 돌아간다.
- Row 단위가 아니라, col 단위로 저장하기 때문에 분석이 용이함
  - 만약 어떤 Collection의 price 평균을 구하겠다고 하면 일반적인 Row 기반 같은 경우에는 필요 없는 데이터도 다 메모리에 올려야하는 문제가 발생
- PK 이외의 Index를 사용할 수 없다는 문제가 있다.
메일 수신 시 개발 DB에는 데이터가 잘 저장되는데, 실 DB에서 데이터가 잘리는 문제가 발생했다.

확인 결과 이모지 뒷 내용이 전부 잘린 것으로 확인해서 DDL을 체크해봤다. (MYSQL을 사용하고 있다.)

그런데 개발 서버 DDL과 실서버 DDL이 동일했다.

그런데 왜 데이터가 개발 서버에서만 잘렸을까..?

## utf8 vs utf8mb4

개발 서버와 실서버의 컬럼은 모두 utf8을 사용하고 있었다.

utf8과 utf8mb4는 어떤 차이가 있을까?
- utf8 - 내부적으로 한 문자당 가변으로 최대 3바이트를 사용한다.
- utf8mb4 - 내부적으로 한 문자당 가변으로 최대 4바이트를 사용한다.

여기서 이모지는 4바이트를 차지한다. 즉 utf8mb4로는 표현할 수 있고, utf8로는 표현할 수 없다.

요즘은 이모지를 많이 사용함에 따라 utf8mb4로 사용하는 것이 일반적이라고 한다.

## DDL이 똑같다며!

그런데 문제는 DDL이 동일하니까 둘다 되거나, 둘다 안되야지 왜 한쪽만 되느냐는 것이다.

MySQL에서는 DB 서버, 테이블, 컬럼, DB 클라이언트 별로 전부 다른 charset을 지정할 수 있다.

아래 SQL을 통해 각 서버의 정확한 설정을 비교해보도록 했다.

```
mysql> show variables like 'char%';
mysql> show variables like 'collation%';
```

개발 DB
- character_set_client - utf8mb4
- character_set_connection - utf8mb4
- character_set_database - utf8mb4
- character_set_filesystem - binary
- character_set_results - utf8mb4
- character_set_server - utf8
- character_set_system - utf8
- character_sets_dir - /usr/share/percona-server/charsets/
- collation_connection - utf8mb4_general_ci
- collation_database - utf8mb4_unicode_ci
- collation_server - utf8_general_ci

실 DB
- character_set_client - utf8mb4
- character_set_connection - utf8mb4
- character_set_database - utf8
- character_set_filesystem - binary
- character_set_results - utf8mb4
- character_set_server - utf8
- character_set_system - utf8
- character_sets_dir - /usr/share/percona-server/charsets/
- collation_connection - utf8mb4_general_ci
- collation_database - utf8_general_ci
- collation_server - utf8_general_ci

추가로 각 DB 서버의 테이블의 Collation도 조회했다.

```
mysql> show table status from schema like '{table}';
```

개발 DB
- utf8mb4_unicode_ci

실 DB
- utf8_general_ci

결과적으로 총 두개가 달랐다.
- character_set_database
- collation
  - Schema
  - Table

이 중 Collation의 경우 정렬과 관련된 데이터이므로, INSERT 시 데이터가 날라가고 저장되는 것과는 관련이 없다고 추측했고 실제로도 관련이 없었다.

확인 결과 각 DB 서버의 Schema Charset이 다른 것이 원인이었으며 아래의 명령어로 변경하면 처리가 가능했습니다.

```
mysql> ALTER DATABASE {schema} CHARACTER SET = {utf8mb4};
```

## 참고
- https://www.lesstif.com/dbms/mysql-rhel-centos-ubuntu-20775198.html
- https://vast.tistory.com/112
- https://www.lesstif.com/dbms/mysql-character-sets-collations-9437311.html

## 테이블 전체 조회

RDB

`select * from table;`

MQL

`db.collection.find({});`

## Index

MongoDB의 Index도 B-Tree 형태를 사용한다.

Index로 item, stock, price가 있다면, item만 조건에 있더라도 인덱스를 활용한다.
- 즉, item 하나만 있는 인덱스는 필요가 없어진다. 

그래서 복합 인덱스를 도메인 요구사항에 맞게 (순서가 중요) 적절한 크기로 유지하면 된다.

Document 안의 데이터로 인덱스를 걸 수도 있다. (Multi-key 인덱스(배열)도 제공함)

Mogno 5.0부터는 인덱스가 간편하고, 큰 주의사항이 없다고 한다.

## Join

MongoDB에서 Join과 Transaction의 경우 느리다. (RDB보다도 느리다. 사상 자체가 비정규화이기 때문에 Join을 고려하지 않음)

그래서 해당 부분을 SR로 날리면, 가능하면 사용하지 마라. 모델링을 바꿔라고 한다.
- 1:1 매핑에서만 사용할 수 있는 정도

그러면 왜 제공하는 기능임에도 사용하지 말라고 하는 걸까..?

MongoDB의 경우 Left Outer Join 형태만 지원한다. MongoDB에서는 LookUp이라고 한다.

해당 부분은 DB Layer에서의 Join보다 Application Layer에서의 Join을 하는 것이 운영 관점에서 더 안정적이다.
- 일반적으로 DB 자원이 Application 자원보다 귀하기 때문 




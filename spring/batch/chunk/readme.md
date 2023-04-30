## Chunk
- 여러개의 Item을 묶은 하나의 블록을 의미한다.
- Chunk 단위로 트랜잭션을 처리한다. (Chunk 단위의 Commit, Rollback)
- 일반적으로 대용량 데이터를 분할 처리할 때 사용한다.

## Chunk<I> vs Chunk<O>
- Chunk<I>는 ItemReader로 읽은 하나의 아이템을 Chunk에서 정한 개수만큼 반복해서 저장하는 타입
- Chunk<O>는 ItemReader로부터 전달받은 Chunk<I>>를 참조해서 가공 후 다음 IterWrite에게 전달하는 타입

## Reader, Processor, Writer
- ItemReader - 단일 item을 읽는다.
- ItemProcessor - 단일 Item을 처리한다.
  - ItemReader의 데이터를 가공해서 ItemWriter로 넘겨주는 것이 가능
  - ItemReader의 데이터를 필터링해서 ItemWriter로 넘겨주는 것이 가능
  - (청크 기반 프로세싱에서 반드시 필요한 것은 아니다.)
- ItemWriter - Items를 일괄로 DB에 저장한다. 

## Cursor vs Paging

배치에서는 실시간적 처리가 어려운 대용량 데이터를 다룰 때 DB I/O의 성능 문제와 메모리 자원의 효율 문제를 해결해준다.

배치에서는 아래의 두 가지 방안을 제시하고 있다.

- Cursor Based Process
  - Jdbc ResultSet을 기본 매커니즘으로 사용
  - 커넥션 연결 1번으로 배치 처리가 완료될 때까지 데이터를 읽어오기 때문에 DB와 SocketTimeout을 넉넉하게 잡아야 한다.
  - 모든 결과를 메모리에 할당하므로 메모리 사용량이 많아지는 단점이 있다.
- Paging Based Process
  - PageSize 만큼 한 번에 메모리로 가지고 온 다음 한 개씩 읽는다.
  - 한 페이지를 읽을 때마다 Connection을 맺고 끊기 때문에 대량의 SocketTimeOut 걱정이 없다.
  - 페이징 단위의 결과만 메모리에 할당하기 때문에 메모리 사용량이 적다.


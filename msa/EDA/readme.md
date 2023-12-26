Event Sourcing
- 도메인의 변경을 이벤트로 발행하는 방식

Event Driven
- 이벤트를 기반으로 특정 작업을 처리하는 모든 방식

즉, Event Driven은 Event Sourcing을 포함한다.

## EDA의 단점

잘못된 한 번의 발행 / 구독이 전체 정합성을 꺠뜨릴 수 있다.
- 원인 파악 및 추적이 매우 어렵다.
- 백업 / 롤백이 반드시 구축되어 있어야 한다.

## EDA가 유용한 경우

- 서버나 DB에서 Sync 요청에 대한 실패가 잦은 경우
- Message Broker
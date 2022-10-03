## Spring Transaction - Propagation(전파 속성)

Spring Transaction의 **Propagation(전파 범위)**에 대해서 알아보자.

트랜잭션 전파 범위는 트랜잭션을 시작하거나 기존 트랜잭션에 참여하는 방법과 롤백되는 범위 등에 대한 속성값이다.

종류는 다음의 7가지가 있다.
- Required**(default)**
- Requires_NEW
- MANDATORY
- SUPPORTS
- NESTED
- NEVER

## Required

default 속성이다.

Required는 부모 트랜잭션이 존재한다면 부모 트랜잭션에 합류한다. 그렇지 않다면 새로운 트랜잭션을 만든다.

중간에 자식 / 부모에서 예외가 발생한다면 자식과 부모 모두 rollback 한다.

## Requires_NEW

무조건 새로운 트랜잭션을 만든다.

nested한 방식으로 메소드 호출이 이루어지더라도 rollback은 각각 이루어 진다.

## MANDATORY

무조건 부모 트랜잭션에 합류시킨다.

부모 트랜잭션이 존재하지 않는다면 예외를 발생시킨다.

## SUPPORTS

메소드가 트랜잭션을 필요로 하지는 않지만, 진행 중인 트랜잭션이 존재하면 트랜잭션을 사용한다. 진행 중인 트랜잭션이 존재하지 않으면 트랜잭션이 없는 채 메소드가 동작한다.

## NESTED

부모 트랜잭션이 존재하면 부모 트랜잭션에 중첩시키고, 부모 트랜잭션이 존재하지 않으면 새로운 트랜잭션을 생성한다.

부모 트랜잭션에 예외가 발생하면 자식 트랜잭션도 rollback한다.

여기까지는 Required와 동일하다.

하지만 자식 트랜잭션에 예외가 발생하더라도 부모 트랜잭션은 rollback하지 않는다.

## NEVER

메소드가 트랜잭션을 필요로 하지 않는다. 만약 진행 중인 트랜잭션이 존재하면 예외가 발생한다.

## 참고
- https://mangkyu.tistory.com/169
- https://wangmin.tistory.com/59?category=948114
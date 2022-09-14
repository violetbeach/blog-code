# JPA - Update(수정) 시 save() 메서드를 호출하는 것이 좋을까?

JPA를 사용하면 트랜잭션 범위 안에서 Dirty Checking이 동작한다.

따라서 save() 메서드를 호출하지 않아도 값이 알아서 수정되고 반영된다.

그렇다면 save()를 호출하는 것이랑 어떤 차이가 있는 지 알아보자.

## 차이
먼저 @Transactional만을 사용한 예제를 보자.
```java
@Transactional
public Notice update(Long noticeId, String content) {
    Notice notice = noticeRepository.findById(noticeId).get();
    notice.setContent(content);
}
```
다음은 repository.save() 메서드를 사용한 예제를 보자.
```java
public Notice update(Long noticeId, String content) {
    Notice notice = noticeRepository.findById(noticeId).get();
    notice.setContent(content);
    noticeRepository.save(notice);
}
```
위 두 코드의 최종 상태는 동일하다.
- 1번 코드의 경우 객체가 자기 할일만 하는 코드이고,
- 2번 코드의 경우 객체의 관점에서 자신의 상태를 변경한 후에, DB에도 따로 반영을 해주는 코드이다.

즉 2번 코드는 외부 인프라인 DBMS를 우려한 코드이고, 객체 지향 관점에서 좋은 형태로 보긴 힘들다.

### 테스트 관점

추가로 고려해야할 점이 테스트이다. 사실 이 문제를 고민하게 된 이유도 테스트때문이다.

Service 클래스에 대한 단위 테스트를 진행하기 위해서는 Repository Mocking이 필요하다.

응용 서비스단에서 save() 메서드를 의미없이 호출하게 되면, Mocking할 메서드가 하나 더 늘어난다.

## 정리

정리하자면 새로운 엔터티를 추가할 때는 repository.save() 메서드 사용을 해야 한다.

하지만, 기존의 엔터티를 수정하는 작업에서는 repository.save() 메서드를 사용하지 않는 것이 더 깔끔하다!

## 참고
- https://github.com/jojoldu/freelec-springboot2-webservice/issues/47
- https://joanne.tistory.com/218
## java.lang.OutOfMemoryError: Java heap space

- 자바 힙 공간에 새롱누 객체를 생성할 수 없을 경우 발생

## java.lang.OutOfMemoryError: GC Overhead limit exceeded

- GC를 수행했지만 새로 확보된 메모리가 전체 메모리의 2% 미만일 때 발생
- 더 이상 GC를 수행할 수 없는 상태가 됨

## java.lang.OutOfMemoryError: Requested array size exceeds VM limit
- 힙 공간보다 큰 배열을 할당하려고 시도할 경우

## java.lang.OutOfMemoryError: Metaspace

- 메타 스페이스에 공간이 부족할 경우
  - 메타 스페이스에는 클래스 로더에 의한 클래스 정보가 저장된다.


## 그 밖에

아래 블로그에서 정리되어 있다.

- https://devhtak.github.io/java%20study/2021/07/29/Java_OOM.html
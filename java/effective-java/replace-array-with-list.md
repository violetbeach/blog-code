## Java -  배열 대신 리스트를 사용해야 하는 이유!

나는 배열과 리스트의 차이가 자원 공간이 동적이냐 아니냐로 생각해왔다.

그래서 자원 공간이 정해져있는 경우에는 배열을 사용했고, 그렇지 않은 경우에는 리스트를 사용해왔다.

하지만 Effective Java를 공부하면서, 많은 블로그를 접하게 되면서 생각이 바뀌게 되었다.

살펴보자.

## Array(배열) vs ArrayList(리스트) In Java

Array와 ArrayList의 차이점을 간단하게 정리해보자.

Array(배열)
- 사이즈가 정적인 데이터 구조
- primitive 타입과 인스턴스 타입 모두 원소로 포함될 수 있다.
- Generic(제너릭)을 사용할 수 없다.
- 부수적인 내용 (길이를 구할때는 length 변수를 사용, 할당은 = 를 사용, ...)

ArrayList(리스트)
- 사이즈가 동적인 데이터 구조
- 인스턴스만 원소로 포함될 수 있다.
- Generic(제너릭)을 지원한다.
- 요소를 반복하는 iterators를 제공한다.
- Collections가 제공하는 다양한 메서드를 사용할 수 있다.
- 부수 적인 내용 (길이를 구할 때는 size() 메서드를 사용, 할당은 add() 메서드를 사용, ...)


# 참고
- https://velog.io/@new_wisdom/Effective-java-item-28.-%EB%B0%B0%EC%97%B4%EB%B3%B4%EB%8B%A4%EB%8A%94-%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC
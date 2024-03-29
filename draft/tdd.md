## TDD - 단위 테스트 작성 순서

단위 테스트의 경우 쉬운 것 / 예외적인 것 -> 어려운 것 / 정상인 것 순으로 작성한다.

암호 검사를 예로 한다.
1. null 입력, 빈 값 입력 -> 예외적인 것 (정상이 아닌 것 - 중요)
2. 모두 충족하는 경우 -> 쉬움
3. 일부 충족하는 경우 -> 그 다음 쉬움
4. 모두 충족하지 않는 경우

## 어렵거나 정상적인 테스트부터 만들면?

어려운 것부터 할 경우
- 작성할 코드가 많고 시간이 오래 걸려서 피드백이 느려진다.

예외적인 것을 뒤에서 테스트할 경우
- 이미 구현한 코드 구조에 영향을 끼칠 수 있다.
    - (기존의 좋은 코드 구조에서 if  문 등이 추가될 수 있음)

### 완급 조절

구현은 생각나는만큼만 조금씩 빠르게 구현한다.
- 단, 테스트를 통과시킬 만큼만 구현한다. (예외 케이스의 경우 초기까지만 작성해도 테스트를 통과할 수 있다.)
- 앞서 가지 말 것

즉, 다양한 테스트를 추가하면서 구현을 구체화해나간다고 생가하면 된다.

### 설게

아마 TDD를 처음 접하시는 분들이 가장 의아해하는 부분이다.

테스트를 실행하려면 해당 클래스, 메서드가 있어야 하는데 어떻게 테스트를 하나..? 컴파일 에러가 나는데..?

사실 켄트백님의 TDD(테스트 주도 개발)의 발행일은 2000년이다.
- 그래서 사실 현재 IDE에서 개발하는 환경에서 적절치 않은 부분이 다소 있다.

아래 세미나 내용을 보면 이에 대해 잘 설명하고 있다.
- https://www.youtube.com/watch?v=rs_ReNmLISw&list=LL&index=1&t=74s

테스트 코드 작성을 위해 Interface를 활용하던지, class의 설계를 먼저 할 수 있다.
- 즉, 완전히 프로덕트 코드보다 테스트 코드를 먼저 완성한다는 개념과는 다르다.



## JUnit - private method 테스트하기

우리는 서비스 단위 테스트를 작성할 때 발생할 수 있는 모든 예외 케이스에 대해 테스트를 진행한다.

그런데 가끔 private method를 테스트하고 싶을 때가 있다.

이때는 어떻게 처리할 수 있는 지 알아보자.

아래는 예시로 사용할 class이다.

```java
public class Magician {
    
    private int level;
    private int exp;
    
    private void levelUp() {
        if(exp >= 100) {
            int count = exp / 100;
            this.level += count;
            exp -= 100 * count;
        }
    }
    
}
```

## 접근이 가능한 메서드에서 테스트

첫 번째는 private method에 대해 테스트를 진행하지 않는 방법이다. 즉, 해당 private method를 호출하는 접근이 가능한 메서드(public, protected, ...)를 테스트하면서 private method를 간접적으로 테스트할 수 있다.

JUnit 창시자 중 한 명이자, TDD 창시자인 켄트 백(Kent Beck)은 이러한 물음에 아래와 같은 Tweet을 남기기도 했다.

![img.png](img.png)

해당 링크를 누르면 No라고 크게 나온다.
- http://shoulditestprivatemethods.com

## Reflection을 활용한 테스트





## 참고
- https://yearnlune.github.io/java/java-private-method-test/#
- https://www.devkuma.com/docs/junit/private-method-test/
- https://okky.kr/articles/860464
- https://mangkyu.tistory.com/235
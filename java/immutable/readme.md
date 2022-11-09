## Java - 불변 객체(Immutable Object)사용을 지향해야 하는 이유! (feat. final 키워드)

Clean Code나 Effective Java는 물론 개발을 잘하는 팀의 얘기를 들으면 불변(Immutable) 객체의 필요성이 빠지지 않는다.

## 불변(Immutable) 객체

불변 객체란 객체 생성 이후 내부의 상태가 변하지 않는 객체이다.

가령 JPA에서 Embedded로 사용하는 Value Object를 예로 들 수 있다.

```java
@EqualsAndHashCode
public class Money {

    private int value;

    public Money(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Money multiply(int multiplier) {
        return new Money(value * multiplier);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
}
```

위 객체는 내부 상태를 변화시키는 Set 메서드가 없다. 그리고 multiply 메서드를 사용해서 새로운 객체를 생성한다.

더 자세한 불변(Immutable) 객체 관련해서는 이전 포스팅을 참고하자.
- https://jaehoney.tistory.com/128
- https://jaehoney.tistory.com/223

그런데 나는 Immutable 객체를 사용하는 이유가 **Heap에 있는 해당 객체의 내용**을 **다른 메서드 블록에서 수정**하거나, **비동기 스레드에서 참조해서 수정하는 등의 처리를 방지**하기 위함정도로 생각했다.

하지만 이외에도 Immutable을 사용하는 이유가 많다. 

## Immutable(불변) 및 final 키워드를 사용해야 하는 이유
1. 위에서 언급했듯 Thread-Safety하고, Call by Reference에 의한 문제를 고려하지 않아도 된다. 
2. 실패 원자적인(Failure Atomic) 메소드를 만들 수 있다.
5. GC의 성능을 높일 수 있다.

### 1. Thread-Safety하고, Call by Reference에 의한 문제를 고려하지 않아도 된다.

자바의 객체는 Heap에 저장된다. 그래서 만약 비동기 쓰레드에서 해당 객체를 수정한다면 메인 쓰레드에서 영향을 받게 된다.

추가로 해당 객체를 가지는 다른 블록에서 해당 객체를 수정할 가능성이 존재한다. (eg. Entity로 만든 Dto에서 Address를 수정하는 경우 -> Entity의 Address도 수정될 염려가 있다.)

Immutable을 사용하면 내가 작성한 메서드를 호출한 이후에는 값이 변하지 않음을 보장받을 수 있다. -> 오류를 줄이고 코드를 간단하게 만들어서 유지보수성이 높은 코드를 작성할 수 있게 된다.

### 2. 실패 원자적인(Failure Atomic) 메소드를 만들 수 있다.

가변 객체를 통해 작업을 하는 도중 예외가 발생하면 해당 객체가 불안정한 상태에 빠질 수 있다.

하지만 불변 객체라면 어떠한 예외가 발생해도 메소드 호출 전의 상태를 유지할 수 있다. -> 즉, 예외가 발생했을 때 기대하는 해당 객체의 상태가 동일하므로 다음 로직을 처리하기가 수월해진다.

### 3. GC의 성능을 높일 수 있다.

많은 분들이 놓치는 Immutable의 이점이 바로 GC의 성능을 높여준다는 것이다.

Java에서는 final 키워드를 사용해서 불변 객체를 생성할 수 있는데, 해당 객체가 참조하는 객체를 먼저 생성하고 해당 객체를 가지는 컨테이너(ImmutableHolder를 생성한다.)


## 참고
- https://mangkyu.tistory.com/131
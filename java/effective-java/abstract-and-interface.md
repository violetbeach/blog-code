# Java - 추상 클래스와 인터페이스 차이

Java 8부터 인터페이스도 default method를 제공할 수 있게 되었다. 즉, 추상 클래스와 인터페이스 모두 인스턴스 메서드를 구현 형태로 제공할 수 있다.

Effective Java에서는 **'가능한'** 추상클래스가 아닌 인터페이스를 활용하는 것을 권장한다.

그 차이는 무엇인지 알아보자.

## Abstract Class vs Interface

해당 두 가지 개념의 가장 큰 차이는 추상 클래스가 정의한 타입을 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 한다는 점이다.

자바는 단일 상속만 지원하니, 추상 클래스 방식은 새로운 타입을 정의하는 데 커다란 제약을 안게 되는 것이다.

<br>

반면, 인터페이스를 구현하면 다른 어떤 클래스를 상속했든 같은 타입으로 취급된다. 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해서 넣을 수도 있다.

<br>

즉, 인터페이스는 Mixin 정의에 적합하다. 예를 들면 Serializable을 구현하면 직렬화가 가능하게 된다. 만약 순서를 정할 수 있는 기능을 추가하고 싶으면 추가로 Comparable을 구현하면 된다.

추상 클래스 경우 기존 클래스가 만약 다른 클래스를 상속하고 있다면, 또 다른 클래스를 상속할 수 없다. 그래서 추상 클래스로 이를 구현하려면 Serializeble과 Comparable을 합친 추상 클래스가 또 필요하게 된다. 이는 추후 유지보수를 하기에 까다로운 존재가 된다.

이러한 점 때문에 **가능한** 추상 클래스보다는 인터페이스를 활용하는 것이 권장한다.

## Abstract Class를 사용하는 경우

앞서 **가능한**이라고 언급한 이유는 인터페이스의 default method에는 아래와 같은 제약이 있기 때문이다.
- Object 메소드인 equals와 hashcode를 default method로 제공하면 안된다.
- 인터페이스는 인스턴스 필드를 가질 수 없고 public이 아닌 정적 메소드를 가질 수 없다.
- 직접 만들지 않은 인터페이스에는 default method를 추가할 수 없다.

## 추상 골격 구현 (skeletal implementation) 클래스

인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다. 인터페이스와 추상 골격 구현 클래스를 함께 제공한다.

인터페이스로 타입을 정의하고 몇 가지 default method를 추가한다.

이후 골격 구현 클래스에서는 필요한 나머지 메서드를 구현한다.

이렇게 해두면 단순히 골격 구현을 확장하는 것만으로 인터페이스를 구현하는 데 필요한 일이 대부분 완료된다. 이것이 `템플릿 메서드 패턴`이다.

```Java
public abstract class AbstractMapEntry<K,V>
        implements Map.Entry<K,V> {
    // 변경 가능한 엔트리는 이 메서드를 반드시 재정의해야 한다.
    @Override public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
    
    // Map.Entry.equals의 일반 규약을 구현한다.
    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?,?> e = (Map.Entry) o;
        return Objects.equals(e.getKey(),   getKey())
                && Objects.equals(e.getValue(), getValue());
    }

    // Map.Entry.hashCode의 일반 규약을 구현한다.
    @Override public int hashCode() {
        return Objects.hashCode(getKey())
                ^ Objects.hashCode(getValue());
    }

    @Override public String toString() {
        return getKey() + "=" + getValue();
    }
}
```

이렇게 구현하면 인터페이스에서 hashCode, equals, toString을 구현하지 않는 원칙을 지키면서 추상 클래스를 이용해서 골격을 제공할 수 있다.

## 추가 사항
Interface는 문서화가 잘 되어 있으면 좋다.  

해당 Interface를 상속하려는 개발자를 위해 @ImpleSpec 자바독 태그를 붙여 문서화하는 것이 좋다.
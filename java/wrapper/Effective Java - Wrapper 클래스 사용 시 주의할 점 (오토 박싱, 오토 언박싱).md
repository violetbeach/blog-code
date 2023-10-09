## Wrapper 클래스

자바에는 기본 타입과 Wrapper 클래스가 존재합니다.

기본(primitive) 타입은 int, long, float, double, boolean 등을 말하고, Wrapper 클래스는 Integer, Long, Float, Double, Boolean 등을 말합니다.

Wrapper 클래스는 기본 타입을 Object로 변환하기 위해 사용합니다. Object를 매개변수로 받는 함수에 기본 자료형을 담기 위해서 사용합니다. 대표적으로는 Collection(List, Set, Map)에 기본 타입을 담을 떄 많이 사용합니다.

#### 객체의 재사용

객체는 일반적으로 무겁기 때문에, 가능하면 재사용하는 것이 좋다는 점은 알고 계실거라 생각합니다. 가독성을 해치지 않는 범위 내에서는 재사용해서 Performance를 얻는 것이죠!

Wrapper 클래스를 사용할 때도 이를 조심해야 합니다.

## 성능 - 오토 박싱, 오토 언박싱

자바에서는 Wrapper 클래스를 더 쉽게 사용할 수 있게 위해서 오토 박싱, 오토 언박싱을 제공합니다. 하지만, 이는 잘 모르고 사용하면 성능 저하를 초래할 수 있습니다.

박싱은 생성자를 사용해서, 객체를 생성함을 명시하고 있지만, 자바에서 제공하는 오토 박싱에서는 마치 기본 타입을 사용하는 것과 차이가 나지 않아 보입니다.

```java
// 박싱
int i = 10;
Integer num = new Integer(i);

// 오토 박싱
Integer i = 10;
```

그래서, 객체를 생성함에 있어 주의해야 되는 부분을 놓칠 수 있습니다. 예를 들어, 아래와 같은 코드를 작성했을 떄를 생각해봅시다.

```java
public static void main(String[] args) {
    Long sum = 0L;
    for (long i = 0; i < 1000000; i++) {
        sum += i;
    }
}
```

겉보기에는 별 문제가 없어보입니다. 기본 타입인 long이 아니라 Wrapper 클래스인 Long을 사용했다는 점을 빼면요. 하지만 이 코드는 내부적으로 아래 코드랑 동일합니다.

```java
public static void main(String[] args) {
    Long sum = new Long(0);
    for (long i = 0; i < 1000000; i++) {
        sum = new Long(0 + sum.longValue());
    }
}
```

위 반복문에서 객체를 100만번을 생성하게 됩니다. CPU와 메모미를 낭비하고 싶지 않다면 기본 타입인 long을 사용하거나 객체를 재사용할 수 있는 새로운 클래스를 정의해야 합니다.

## 불확실성

Integer는 그 자체로 **순서**라는 개념이 존재합니다.

```java
Comparator<Integer> naturalOrder =
        (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);
```

위 코드는 별다른 문제 없이 랜덤 값으로 100만번 테스트를 해도 잘 동작합니다.

문제는 `naturualOrder.compare(new Integer(42), new Integer(42));`를 하면 두 Integer의 값이 같으므로 0을 출력해야 하지만 1을 출력합니다.

즉, 첫 번째 인스턴스가 두 번째 인스턴스보다 크다고 주장합니다. `(i == j)`에서 주소값으로 비교를 수행하기 때문입니다. 박싱된 타입에서 `==`를 사용하면 문제가 생깁니다.

## 마무리 

Wrapper 클래스보다는 기본 타입을 사용하고 의도하지 않은 오토박싱이 숨어 있지 않은지, 성능상 문제가 없는지 확인하는 것이 중요합니다.

감사합니다.
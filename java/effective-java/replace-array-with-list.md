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

## 배열의 문제점

배열은 **공변(Convariant)**이다. 공변이란 무언가가 같이 변한다는 뜻이다.

Sub 타입이 Super 타입의 하위 타입이라고 가정하자. `Sub[]`는 `Super[]`의 하위 타입이 된다. 즉, 함께 변한다.

제너릭은 불공변(Invariant)이다. 제너릭에서 서로 다른 타입은 서로의 하위 타입도 상위 타입도 될 수 없다. <br>
-> 즉, `List<Sub>`는 `List<Super>`의 하위 타입도, 상위 타입도 아니다

제너릭이 이상한 것이라고 보일 수도 있지만, 공변인 배열은 아래의 문제가 발생할 수 있다.
```java
Object[] objectArr = new Long[1];
objectArray[0] = "String" // ArrayStoreException 발생!
```
즉, 문법상 허용이 되지만 런타임에서 예외가 터질 수 있다.

반면 List의 경우에는 바로 컴파일 에러가 발생해서 빌드 자체가 안된다.
```java
List<Object> objectList = new ArrayList<Long>();
objectList.add("String")
```

`E`, `List<E>`, `List<String>` 같은 타입을 실체화 불가 타입(non-reifiable type)이라고 한다. 제너릭 소거로 인해 실체화 되지 않아서 런타임 시점에 컴파일 시점보다 타입 정보를 적게 가지는 타입을 말한다.

리스트와 배열 간 형변환할 때 제너릭 배열 생성 오류나 비검사 형변환 경고가 뜨는 경우 대부분 `E[]` 대신에 컬렉션인 `List<E>`를 사용하면 해결된다.

아래 List를 Array로 변환하는 예시를 보자.
```java
public class Chooser {
    private final Object[] choiceArray;

    public Chooser(Collection<T> choices) {
        choiceArray = choices.toArray();
    }

    // 컬렉션 안의 원소 중 하나를 무작위로 선택해 반환
    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceArray.size()));
    }
}
```
위 코드는 choose() 메서드를 호출할 때마다 타입 변환이 이루어지면서, 런타임 에러가 날 가능성이 존재한다.

다음은 제너릭을 사용한 클래스를 보자.
```java
public class Chooser<T> {
    private final T[] choiceArray;

    public Chooser(Collection<T> choices) {
        choiceList = choiceArray.toArray();
    }
    ...
}
```
해당 코드는 컴파일되지 않는다. Collection의 toArray() 메서드가 Object[]를 반환하기 때문이다.

이를 형변환하려면 `choiceArray = (T[]) choices.toArray();`처럼 타입을 변환해야 하지만, 런타임에도 안전할 수 없다는 경고 메시지가 나오게 된다. 따라서 해당 코드는 지양하는 것이 좋다.

리스트를 사용하면 해당 문제를 깔끔하게 해결할 수 있다.
```java
public class Chooser<T> {
    private final List<T> choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```
해당 코드는 컴파일 시점에 모든 에러를 찾을 수 있으므로 경고가 발생하지 않는다.


## 결론

배열은 리스트에 비해 **효율적(처리 속도, 자원 공간 등)**이라는 장점이 있다.

배열은 공변이고 실체화되는 반면, 제너릭은 불공변이고 실체화된다. 배열보다는 제너릭을 사용하는 리스트를 사용하는 것이 더 안전하다.

따라서, 배열과 리스트를 섞어서 사용하거나 배열을 명시적으로 사용할 수 없을 때는 **배열 대신 리스트를 사용**하는 방법을 고려해보는 것이 좋다. 

# 참고
- https://velog.io/@new_wisdom/Effective-java-item-28.-%EB%B0%B0%EC%97%B4%EB%B3%B4%EB%8B%A4%EB%8A%94-%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC
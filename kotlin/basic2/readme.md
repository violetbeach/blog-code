## 코틀린 컴파일러

코틀린 컴파일러는 JVM에서 실행될 수 있는 바이트코드가 포함된 클래스파일을 생성한다. 자바 코드를 생성하는 것이 아니다.

자바 컴파일러는 체크 예외를 핸들링하는 지 여부를 검사하지만, 코틀린 컴파일러는 해당 부분에 대해 검사하지 않는다.

## \@field

Kotlin으로 Valid의 제약사항을 명시할 때는 `@field:`를 prefix로 붙여야 한다. Java 방식대로 `@NotNull`로 사용하면 컴파일러에서 생성자에 애노테이션을 붙인다.

아래의 코틀린 코드를 가정해보자.

```kotlin
data class Request(
    @field:NotNull
    val value: String
)
```

코틀린 컴파일러 입장에서는 Getter, Setter에 애노테이션을 붙일지, 필드에 붙일지, 생성자에 붙일 지 확실치 않다. 그래서 어노테이션을 붙일 위치(use site)를 명시해줘야 한다.

- file
- property
- field
- get
- set
- ...

## 불변성

코틀린은 가변보다 불변을 기본적으로 선호한다. 클래스 역시 기본적으로 final로 선언된다.

## 원시 타입과 래퍼 타입

코틀린에서는 원시(primitive) 타입과 래퍼(wrapper) 타입을 구분하지 않는다. 일반적으로 원시 타입으로 컴파일 되고 제너릭 클래스를 사용하는 등 불가피한 상황에서는 래퍼 타입을 사용한다.

## 플랫폼 타입

코틀린에서 자바 코드를 사용할 때 null 관련 정보를 알 수 없는 경우 플랫폼 타입으로 만든다. 플랫폼 타입은 모든 연산에 대한 책임을 개발자에게 위임한다.

아래 코드는 자바로 작성한 에시이다.

```java
public class JavaClass {
    public String getValue() {
        return null;
    }
}

```

해당 코드를 사용하는 코틀린 코드를 보자.


```kotlin
fun statedType() {
    val value: String = JavaClass().value // NPE
    // ...
    println(value.length)
}

fun platformType() {
    val value = JavaClass().value
    // ...
    println(value.length) // NPE
}
```

두 코드는 모두 NPE가 발생한다. 이런 자바 코드는 `@Nullable` 애노테이션을 붙이거나 제거하는 것이 좋다. 

## by 대리자

일급 객체, 일급 컬렉션을 사용할 때 매우 유용하게 사용할 수 있는 것이 `by 대리자` 이다.

```kotlin
data class Users(
    val users: List<User>,
) : List<User> by users

val isEmpty = users.isEmpty()
```

## Expression body

코틀린은 간단한 함수를 작성할 때 `{}`와 같은 block 방식이 아닌ㄴ `=`를 활용한 expression body 사용을 권장한다.

## Sequence

코틀린은 병렬 처리가 필요할 경우에서만 자바의 스트림을 사용하고, 그 외는 코틀린의 싀퀀스를 사용하는 것이 좋다고 한다.

```kotlin
val numbers = sequenceOf("four", "three", "two", "one")
```

## 참고

- https://mangkyu.tistory.com/358
- https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets

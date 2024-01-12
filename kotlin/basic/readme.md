## 코틀린

코틀린은 러시아의 상트페테르부르크의 섬 이름이다. 해당 지역 출신의 깨발자가 많아서 코틀린이 되었다.

코틀린은 안전성, 간결성, 상호 운용성을 강조하는 다중 패러다임, 다중 플랫폼 프로그래밍 언어이다. 다중 패러다임은 객체지향 프그래밍이나 함수형 프로그래밍 방식 등을 말한다.

## JVM 언어

코틀린은 JVM언어 중 하나이다.

그렇다고 코틀린이 직접 바이너리 프로그램을 작성하는 것은 아니고, 빌드의 산출물이 Java 클래스 파일이다.

즉, 코틀린은 자바 프로그램을 작성하는 언어라고 보는 것이 맞다.

## 새로운 개념

아래는 자바를 기준으로 한 코틀린의 새로운 개념이다. 

#### 타입 추론

코틀린은 변수 타입을 지정하지 않아도 **타입추론**으로 컴파일되고 실행된다.

코틀린은 **강한 타입 지정 언어**임에도 개발자가 불필요한 타입 정보를 코드에 추가하지 않을 수 있다.

#### 프로퍼티

자바에서 클래스가 가지는 멤버변수를 기본적으로 필드라고 불렀다.

코틀린에서는 프로퍼티라고 부른다.

클래스 내부에 프로퍼티를 선언하면 게터, 세터가 자동으로 생성된다. (val 변수는 게터만 자동으로 생성된다.)

## 문법

자바를 기준으로 해서 코틀린 문법을 알아보자.

#### 세미콜론

세미콜론을 생략할 수 있다.

```kotlin
println("Hello")
```
#### 불변 & 가변

- 불변 변수 - val
- 가변 변수 - var

#### 논리

- 논리합 즉시(eager) 계산: or
- 논리합 지연(lazy) 계산: || 
- 논리곱 즉시(eager) 계산: and
- 논리곱 지연(lazy) 계산: &&

중요한 점은 즉시 계산이 먼저 수행된다는 점이다. 즉시 계산의 경우 자바의 `&`나 `|`와 동일하다. 즉, 비트 연산도 해당 연산자로 가능하다.

#### 문자열 템플릿

아래와 같은 문자열 템플릿을 제공한다.

```kotlin
val name = "john"
println("Hello, $name! ${Date()}")
```

#### 배열

배열 선언 시 new 연산자를 사용하지 않는다. 

```kotlin
val a = emptyArray<String>()            // Array<String> (원소 0개)
val b = arrayOf("Hi", "Hello", "Aloha") // Array<String> (원소 2개)
```

아래와 같이 배열 덧셈도 가능하다.

```kotlin
val a = intArrayOf(1,2) + 3
val b = intArrayOf(1,2) + intArrayOf(3,4)
```

비교는 아래와 같이 할 수 있다.

```kotlin
a.contentEquals(b)
```

#### Null

```kotlin
val a = readLine()!!.toInt()
```

`!!`는 null이 아님을 단언(assertion)하는 것이다. `readLine()`의 결과가 null일 경우 예외를 발생한다.

아래는 다른 문법이다.
- ? - null 허용 (기본적으로 허용 X)
- ?. - Null이 아닌 경우에만 호출 (null인 경우 null 반환)
- ?: - null이 아닌 사용자 정의 값을 반환하고 싶을 때 사용
- !! - null이 아님을 컴파일러에게 보증 (null이면 NPE)

#### 조건

```kotlin
// if (someObject != null && status) {
//    doThis()
// }

someObject?.takeIf{ status }?.doThis()
```

코틀린에서는 `taskIf`를 사용해서 조건 절에 null을 활용할 수 있다. `taskIf`는 인자인 predicate가 true일 경우 그 값을 return하고, 아닐 경우 null을 return한다.

반대로 `takeUnless`는 인자로 들어오는 predicate가 false일 경우 그 값을 return 한다.

#### Type

코틀린의 모든 타입은 Any의 하위 타입이다.

Float, Double은 INFINITY 연산도 지원한다.

#### Class

- open: 클래스 앞에 `open` 키워드가 없으면 상속이 불가능
- object: 클래스를 정의하면서 동시에 인스턴스를 생성 (싱글톤)
- sealed: 추상 클래스와 유사 (내부적으로 여러 클래스를 가짐)
- data class
    - toString, hashCode, equals, copy를 자동으로 구현
    - 불변을 의미하지는 않음
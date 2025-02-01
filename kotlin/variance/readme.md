코틀린에서 제너릭을 잘 활용하기 위해서는 주요 개념 중 하나인 **변성**을 이해해야 한다.

변성에 대해 알아보자.

## 변성의 정의

변성(Variance)은 기저 타입(Base type)이 같으면서 타입 인자(Type argument)가 다른 경우 서로 어떤 관계에 있는 지 설명할 때 사용하는 개념이다.

기저 타입이랑 타입 인자도 생소할 것이다.

`List<Int>`가 있으면 `List`가 기저 타입(Base Type)이고, `Int`가 타입 인자(Type argument)이다.

기저 타입과 타입 인자가 어떤 관계인지이고, 어떻게 사용할 지에 따라 변성을 지정할 수 있다. 변성을 잘 활용하면 확장성과 타입 안정성을 동시에 챙길 수 있다.

## 변성이 필요한 이유

#### 서브 타입(Sub Type)

아래 코드를 보면 타입에 의한 컴파일 에러가 날 것이라고 생각할 수 있다.

```kotlin
fun main() {
    val numbers: List<Int> = listOf(1, 2, 3)
    printList(numbers) // 컴파일 에러 X
}

private fun printList(list: List<Any>) {
    println(list.joinToString())
}
```

하지만 컴파일 에러가 나지 않는다. 코틀린에서 `List<Int>`는 `List<Any`의 하위 타입이다.

다음 코드를 보자.

```kotlin
fun main() {
    val numbers: MutableList<Int> = mutableListOf(1, 2, 3) // MutableList<Int>
    printList(numbers) // 컴파일 에러 O
}

private fun printList(list: MutableList<Any>) {
    println(list.joinToString())
}
```

`List`를 `MutableList`로 바꿨을 뿐인데 컴파일 에러가 발생한다.

왜 이런 차이가 발생할까?

#### 서브 타입의 정의

A 타입의 모든 기능을 B 타입이 수행할 수 있을 때 B타입을 A타입의 **서브타입**이라고 한다.

`List<Int>`는 `List<Any>`의 모든 기능을 수행할 수 있다. 즉, `List<Int>`는 `List<Any>`의 하위 타입이다.

`MutableList<Int>`는 `MutableList<Any>`의 모든 기능을 수행할 수 있을까?
- `MutableList<Any>`는 `list.add("A")`를 수행할 수 있다.
- `MutableList<Int>`는 `list.add("A")`를 수행할 수 없다.

예시처럼 가변성이 있는 `MutableList<Int>`는 `MutableList<Any>`의 하위 타입이 아니다.

---

확장성을 가지면서 타입 안정성을 보장하기 위해서는 위와 같은 개념을 활용해서 제너릭을 올바르게 사용해야 하고, 그래서 변성에 대한 이해가 필요하다.


## 변성의 세 가지 유형

가변성에는 세 가지 유형이 있다. 공변성, 반공변성, 무변성이다.

1. `<out T>`: 공변(Covariance)
2. `<in T`>: 반공변(Contravariance)
3. `<T>`: 무공변(invariance)

#### 1. 공변(Covariance)

공변(Covariance)는 `Covariance<Int>`와 `Covariance<Number>` 처럼 A 타입이 B 타입의 하위 타입이 되는 경우를 말한다.

코틀린에서는 `out` 키워드를 사용해서 제너릭의 변성을 **공변**으로 지정할 수 있다.

```kotlin
class Covariance<out T: Number>
```

아까 예시에서 본 `List` 클래스가 공변에 해당한다.

![img.png](img.png)

그래서 `List<Any>` 파라미터의 인자로 `List<Int>`가 전달될 수 있었다. 공변은 변하지 않는 데이터에 한해 확장성을 제공해준다.

#### 2. 반공변(Contravariance)

반공변은 공변과 반대이다. `Contravariance<Number>`와 `Contravariance<Int>`처럼 A 타입이 B 타입의 상위 타입이 되는 경우를 말한다.

코틀린에서는 `in` 키워드를 사용해서 제너릭의 변성을 **반공변**으로 지정할 수 있다.

```kotlin
class Contravariance<in T>
```

대표적인 예시는 `Comparable` 클래스이다. 

![img_2.png](img_2.png)

반공병성은 주로 입력 타입에 제약을 두고 싶을 때 사용한다.

TBD


#### 3. 무공변(Invariance)

변성을 지정하지 않으면 기본적으로 **무공변** 상태가 된다.

```kotlin
class Invariance<T>
```

`MutableList`는 무공변에 해당한다.

![img_1.png](img_1.png)

그래서 `MutableList<Any>` 파라미터의 인자로 `MutableList<Int>`가 전달될 수 없었다.

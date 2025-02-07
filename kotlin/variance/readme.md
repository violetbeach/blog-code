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

**공변(Covariance)**은 `Int`, `Number`처럼 A 타입이 B 타입의 하위 타입이고, `SomeClass<Int>`도 `SomeClass<Number>`의 하위 타입이 되는 경우를 말한다.

코틀린에서는 `out` 키워드를 사용해서 제너릭의 변성을 **공변**으로 지정할 수 있다.

```kotlin
class Covariance<out T: Number>
```

아까 예시에서 본 `List` 클래스가 공변에 해당한다.

![img.png](img.png)

그래서 `List<Any>` 파라미터의 인자로 `List<Int>`가 전달될 수 있었다. 공변은 변하지 않는 데이터에 한해 확장성을 제공해준다.

#### 2. 무공변(Invariance)

변성을 지정하지 않으면 기본적으로 **무공변** 상태가 된다.

```kotlin
class Invariance<T>
```

`MutableList`는 무공변에 해당한다.

![img_1.png](img_1.png)

그래서 `MutableList<Any>` 파라미터의 인자로 `MutableList<Int>`가 전달될 수 없었다.

#### 3. 반공변(Contravariance)

반공변은 공변과 반대이다. `Int`, `Number`처럼 A 타입이 B 타입의 하위 타입이지만, `Contravariance<Int>`가 `Contravariance<Number>`의 상위 타입이 되는 경우를 말한다.

코틀린에서는 `in` 키워드를 사용해서 제너릭의 변성을 **반공변**으로 지정할 수 있다.

```kotlin
class Contravariance<in T>
```

대표적인 예시는 `Comparable` 클래스이다.

![img_2.png](img_2.png)

`Comparable<Long>`의 모든 기능은 `Comparable<Any>`가 수행할 수 있다.

반공변이 왜 필요한 지 이해해보자.

아래는 무공변을 사용해서 Cooker 클래스를 만든 예시이다.

```kotlin
interface Cooker<T> {
    fun cook(item: T)
}

class FoodCooker : Cooker<Food> {
    override fun cook(item: Food) {
        println("Cooking ${item.name}")
    }
}

fun main() {
    val cooker: Cooker<Food> = FoodCooker()
    cooker.cook(Ramen()) // 컴파일 에러!!
}
```

무공변에서 `Cooker<Food>`와 `Cooker<Ramen>`는 관계가 없으므로 컴파일 에러가 발생한다.

아래는 반공변을 활용한 예시이다.

```kotlin
interface Cooker<in T> {
    fun cook(item: T)
}

class FoodCooker : Cooker<Food> {
    override fun cook(item: Food) {
        println("Cooking ${item.name}")
    }
}

fun main() {
    val cooker: Cooker<Ramen> = FoodCooker()
    cooker.cook(Ramen()) // 정상 동작!
}
```

`Cooker<Ramen>`의 하위타입으로 `Cooker<Food>`가 허용되었다.

## 다형성을 위한 유연성

위 내용을 잘 보면 아래의 사실을 확인할 수 있다.
- 공변은 A 타입의 하위 타입의 값을 반환할 수 있다.
- 반공변은 A 타입의 하위 타입의 값을 인자로 입력받을 수 있다.

프로그래밍 언어는 타입 안정성을 위해 제너릭을 지원한다. 다만, 무공변만으로는 다형성을 활용한 유연한 인터페이스를 만들기 어렵다. 

공변은 쓰기(Write)를 할 때 하위 타입의 입력을 허용함으로써 다형성을 위한 유연성을 제공한다.
반면, 반공변은 읽기(Read)를 할 때 하위 타입의 반환을 허용함으로써 다형성을 위한 유연성을 제공한다.

공변을 잘 알고 사용한다면 넓은 의미의 인터페이스(Interface, Class, ...)를 보다 유연하게 만들 수 있을 것이다.

## 참고

- https://jaeyeong951.medium.com/kotlin-in-n-out-variance-변성-69204cbf27a1
- https://www.yes24.com/product/goods/55148593


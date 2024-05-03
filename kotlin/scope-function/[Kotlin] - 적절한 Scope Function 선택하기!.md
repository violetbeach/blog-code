![img.png](img.png)

Kotlin 표준 라이브러리에서 객체의 컨텍스트 안에서 특정 블록의 코드를 실행하는 것이 목적인 함수가 포함되어 있다.

코틀린에서는 Scope Functions을 제공하고 목적에 맞게 선택하는 것을 권장한다.

나는 대부분 만능에 가까운 `let`을 사용했고, Scope Function을 선택하는 기준을 모르고 있었다. 해당 포스팅은 공식문서 기반으로 Scope Function을 선택하는 기준에 대한 내용이다.

## Spec

다음은 각 Scope Function에 대해 정리한 것이다. 

|Function| 참조 객체  | 반환값            | 확장함수 여부                  |
|------|--------|----------------|--------------------------|
|`let`| `it`   | Lambda result  | Y                        |
|`run`| `this` | Lambda result  | Y                        |
|`run`| -      | Lambda result  | N: 객체 Context 밖에서 실행된다.  |
|`with`| `this` | Lambda result  | N: 객체 Context를 인수로 사용한다. |
|`apply`| `this` | Context object | Y                        |
|`also`| `it`   | Context object | Y                        |

## 사용 용도

공식 문서에서 정의한 각 Scope Function의 용도는 아래와 같다.
- let:
  - nullable이 아닌 객체에 대한 람다 실행
  - 로컬 볌위의 변수로 표현식을 도입한다.
- apply
  - 객체를 설정한다.
run:
  - 객체를 생성하고, 특정 동작을 수행한다.
  - 표현식이 필요한 동작 수행
- also
  - 부가적인 효과
- with
  - 객체의 함수 그룹핑

Scope Function을 사용하면 코드가 간결해질 수 있고, Kotlin을 잘 아는 듯한 느낌이 들 수 있다.

하지만 공식문서에서는 Scope Function은 코드를 읽기 어렵게 만들고, 현재 Context의 객체와 `this`, `it`의 혼동으로 인한 오류를 야기할 수 있다고 한다. Scope Function의 중첩은 피하고 연결 시 충분히 주의를 해야 한다.

코틀린의 Scope Function의 가장 큰 2개의 차이는 다음과 같다.

1. Context Object 참조
2. 반환값

해당 2가지에 의해서 사용할 Scope Function을 선택하면 된다.

## 기준 1. Context Object

Scope Function에 전달된 람다 내에서 Context Object에는 Reference로 접근할 수 있는 방법을 제공한다.

이때 Scope Function에 따라 아래 두 가지 방법이 있다.
1. Lambda receiver - this
2. Lambda argument - it 

아래 코드를 보자.
```kotlin
fun main() {
    val str = "Hello"
    // this
    str.run {
        println("The string's length: $length")
        //println("The string's length: ${this.length}") // does the same
    }

    // it
    str.let {
        println("The string's length is ${it.length}")
    }
}
```

`run`의 경우 Lambda receiver를 사용하고 있고, `let`의 경우에는 Lambda argument를 사용하고 있다.

#### Lambda receiver

`run`, `with`, `apply`에서는 Lambda receiver를 사용해서 Context Object에 접근한다.

Lambda Receiver의 경우에는 대부분 `this`를 생략해서 코드를 더 짧게 만들 수 있다. 그래서 외부 객체와의 구별에 주의해야 한다. 

```kotlin
val adam = Person("Adam").apply { 
    age = 20
    city = "London"
}
```

특정 객체의 기능을 호출하거나 필드에 값을 할당하는 동작에서는 Lambda receiver를 사용하는 것이 좋다. 

#### Lambda argument

`let`, `also`의 경우는 Lambda argument를 사용한다. 인수의 이름은 지정할 수 있으며 기본적으로 `it`이 사용된다.

```kotlin
fun getRandomInt(): Int {
    return Random.nextInt(100).also {
        writeToLog("getRandomInt() generated value $it")
    }
}
```

객체가 자주 사용되거나 특정 함수의 인수로 사용될 때는 Lambda argument를 사용하는 것을 권장한다.

## 기준 2. Return Value

Lambda receiver를 사용할 지 Lambda argument를 사용할 지 정했으면 Return 값에 맞게 선택하면 된다.

- `apply`, `also`는 Context Object를 반환한다.
- `let`, `run`, `with`은 Lambda Result를 반환한다.

Scope Function을 선택을 할 때는 Context Object를 필요에 따라 선택하고, 이후에 Retrun Value를 선택하면 된다.

`let`은 대부분 모든 상황에서 사용할 수 있다. 그렇지만 **적절한 Scope Function을 선택**하면 코드의 가독성을 증대시키고 혼동을 줄일 수 있다.

## 참고
- https://kotlinlang.org/docs/scope-functions.html
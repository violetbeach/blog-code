## 코틀린

코틀린은 러시아의 상트페테르부르크의 섬 이름이다. 해당 지역 출신의 깨발자가 많아서 코틀린이 되었다.

코틀린은 안전성, 간결성, 상호 운용성을 강조하는 다중 패러다임, 다중 플랫폼 프로그래밍 언어이다. 다중 패러다임은 객체지향 프그래밍이나 함수형 프로그래밍 방식 등을 말한다.

코틀린의 주 목적은 자바가 사용되는 모든 용도에 더 간결하고, 생산적이고, 안전한 언어를 제공하는 것이다.

## JVM 언어

코틀린은 JVM언어 중 하나이다.

그렇다고 코틀린이 직접 바이너리 프로그램을 작성하는 것은 아니고, 빌드의 산출물이 클래스 파일이다.

![img.png](img.png)

코틀린은 자바 프로그램을 작성하는 언어라고 보는 것이 맞고, 컴파일 후 java 명령어로 해당 코드를 실행한다.

## 새로운 개념

아래는 자바를 기준으로 한 코틀린의 새로운 개념이다. 

#### 타입 추론

코틀린은 변수 타입을 지정하지 않아도 **타입추론**으로 컴파일되고 실행된다.

코틀린은 **강한 타입 지정 언어**임에도 개발자가 불필요한 타입 정보를 코드에 추가하지 않을 수 있다.

#### 변수와 함수

코틀린에서는 클래스 외부에도 변수나 함수를 선언할 수 있다.

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

- 불변 변수 - val (value)
- 가변 변수 - var (variable)

#### 논리

- 논리합 즉시(eager) 계산: or
- 논리합 지연(lazy) 계산: || 
- 논리곱 즉시(eager) 계산: and
- 논리곱 지연(lazy) 계산: &&

중요한 점은 즉시 계산이 먼저 수행된다는 점이다. 즉시 계산의 경우 자바의 `&`나 `|`와 동일하다. 즉, 비트 연산도 해당 연산자로 가능하다.

#### 문자열

아래와 같은 문자열 템플릿을 제공한다.

```kotlin
val name = "john"
println("Hello, $name! ${Date()}")
```

코틀린에서 `"""`를 사용하면 이스케이프 문자를 사용하지 않아도 된다.
`"""(.+)/(.+)\\.(.+)"""`


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

타입 변환을 안전하게 하기 위해 `as?`를 사용할 수도 있다.

![img_1.png](img_1.png)

또는 let을 사용해서 직접 구현할 수도 있다.

```kotlin
email?.let { sendEmailTo(it) } // email이 null일 경우 아무것도 하지 않음
```


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

##### Unit

코틀린은 자바의 void 대신 Unit이라는 클래스를 사용한다.

코틀린은 primitive type을 일체 사용하지 않고, 모든 타입을 클래스로 만들어서 사용한다.
- 컴파일 시 자바의 primitive과 wrapper 중 효율적인 방식으로 자동 변환된다.
- null이 될 수 있는 경우 wrapper 타입으로 컴파일

그래서 모든 타입은 Any(자바의 Object)를 상속받고 있고, void의 경우 Unit으로 래핑해서 사용하고 있다.

##### 변환

코틀린은 타입의 자동 변환을 지원하지 않는다. 

```kotlin
val i = 1
val l: Long = i //Error: type mismatch 컴파일 에러 발생
```

대신 Boolean을 제외한 모든 타입에 대한 변환 메서드를 제공한다.

```kotlin
val i = 1
val l: Long = i.toLong()
```

##### 파라미터

코틀린에서는 파라미터가 널(Null)을 허용할 지 여부를 `?`로 나타낸다.
- `override fun onCreate(savedInstanceState: Bundle?) {}`

#### Class

클래스 앞에 아래 키워드를 붙일 수 있다.
- open: 상속이 가능 (final의 반대. Kotlin은 final이 default라고 이해하면 된다.)
  - 코틀린의 철학은 의도한 것이 아니라면 모두 final 클래스로 만들어야 한다는 것이다.
- sealed: 추상 클래스와 유사 (내부적으로 여러 클래스를 가짐)
  - when 구문을 사용할 때 모든 내부 클래스를 명시하면 else를 사용하지 않아도 된다.
- data:
    - toString, hashCode, equals, copy를 자동으로 구현
    - 불변을 의미하지는 않음

**object 키워드**

```kotlin
object HelloPrinter {
    fun print(content: String) : Unit {
        println("Hello $content")
    }
}
```

```kotlin
HelloPrinter.print("VioletBeach")
```

object 키워드는 환경에 따라 주입 관계를 바꾸기가 어려우므로 DI 프로젝트와 클래스를 사용하는 것이 더 좋은 설계가 될 가능성이 크다.

싱글톤 클래스를 정의할 때 사용한다. 사용처에서는 아래와 같이 자바의 static 클래스를 사용하는 듯하게 사용한다.

익명 객체를 만들 때도 아래와 같이 object 키워드를 사용한다.

```kotlin
val listener = object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) { ... }
    override fun mouseEntered(e: MouseEvent) { ... }
}
```

**생성자**

아래는 모두 코틀린의 생성자의 문법이다.

```kotlin
class User constructor(nickname:String){
    val nickname: String
    init {
        nickname = nickname
    }
}

class User(nickname: String){
  val nickname = nickname;
}

class User(val nickname: String)

class User(val nickname: String, val isSubscribed: Boolean = true)

class User {
  val name: String
  val age: Int

  constructor(name: String): this(name, 0) {}

  constructor(name: String, age: Int) {
    this.name = name
    this.age = age
  }
}
```

#### when

코틀린은 switch 대신 when 구문을 사용한다. 

```kotlin
fun main() {
    val x = 5

    when (x) {
        1 -> println("x는 1이다")
        2, 3 -> println("x는 2 또는 3이다")
        in 4..10 -> println("x는 4와 10 사이에 있다")
        else -> println("x는 다른 수이다")
    }
}
```

when은 switch랑 다르게 상수 뿐 아니라 객체의 사용도 허용한다.

#### is

코틀린은 instanceof 대신 is를 사용하여 변수 타입을 검사한다.

```kotlin
fun floatToInt(e: Number): Int {
    if (e is Float) {
        return e.toInt();
    }
    throw IllegalArgumentException("Unknown expression")
}
```

위에서 보면 코틀린은 자바에서 처럼 (Num)과 같이 명시적으로 캐스팅하지 않아도 된다.

이를 **스마트 캐스트**라고 한다.

스마트 캐스트는 1.3부터 다른 함수의 라인까지 인식해서 높은 수준으로 지원한다.

#### 상수

코틀린에서는 `static`이나 `const` 키워드를 사용하지 않는다.

상수를 정의하는 방법은 2가지가 있다.

첫 번째로 companion object를 사용하는 방식이다.

```kotlin
class Rule{
    companion object{
        const val Admin:Int = 1

        fun printAdmin() {
            println(Admin)
        }
    }
}
```

두 번째는 **패키지 변수**로 선언하는 방식이다.

```kotlin
package org.violetbeach.rule.constants

val Admin:Int = 1

fun printAdmin() {
    println(Admin)
}
```

## 확장 함수

코틀린은 다른 클래스의 인스턴스 메서드를 추가할 수 있다. 이를 **확장 함수**라고 한다.

```kotlin
fun String.lastChar(): Char = this.get(this.length - 1)
```

그러면 아래와 같이 String 객체의 인스턴스 메서드처럼 호출이 가능해진다.

```kotlin
println("Hello".lastChar())
```

확장 함수는 오버라이드가 안되고, 이름과 파라미터가 같을 경우 멤버 함수가 호출된다.

이를 활용하면 자바에서 복잡한 디자인 패턴을 적용하지 않고도, 써드 파티 라이브러리에 함수를 추가할 수 있다.

## 컬렉션

코틀린의 컬렉션은 자바와 같은 클래스를 사용함에도 더 확장한 API를 제공한다.

그렇다. 확장 함수를 사용하기 때문이다.

다양한 메서드는 기본이고, 가변 인자도 사용할 수 있고, 배열과의 자유로운 변환도 가능하다.

## lazy

lateinit은 나중에 값을 할당할 경우 사용할 수 있다. 예를 들어 아래 코드를 보자.

```kotlin
fun initialize() {
    var str: String = "";
    if(age < 10) {
        str = "아이입니다."
    }
    if(age > 20) {
        str = "성인입니다."
    }
    description = str;
}
```

여기서 실수로 str이 초기값인 ""가 될 수 있다. 기본값을 사용하지 않고 반드시 초기화를 해야 함을 명시하려면 아래와 같이 사용할 수 있다.

```kotlin
fun initialize() {
    lateinit var str: String
    if(age < 20) {
        str = "아이입니다."
    }
    if(age >= 20) {
        str = "성인입니다."
    }
    description = str;
}
```

lateinit은 var 속성에 사용하고, lazy 는 람다를 받는다.

```kotlin
lateinit var inputValue : String
val x : Int by lazy { inputValue.length }
inputValue = "Initialized!"
println(x)
```

x가 처음 사용되는 순간 lazy 문의 결과로 초기화시킨다.

## by

by 키워드를 사용하면 delegate 패턴과 유사하게 특정 동작을 다른 클래스에 위임할 수 있다.

```kotlin
class CountingSet<T>(
        val innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet {

    var objectsAdded = 0

    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}
```

해당 메서드에서는 `add()`, `addAll()`을 제외하고 innserSet에 메서드 구현을 위임한다.

## inline

인라인 함수를 사용하면 람다식을 사용할 때 무의미한 객체 생성을 막을 수 있다.

```kotlin
inline fun inlined(block: () -> Unit) {
    block()
}

fun doSomething() {
    inlined { println("do something") } 
}
```

단, inline 메서드는 람다를 인자로 받는 함수에서만 사용하길 권장한다.

일반 함수의 경우 JVM이 강력하게 인라이닝을 제공한다.

## Iterator

코틀린의 Iterator는 아래 특징을 가진다.
- 순서대로만 액세스할 수 있다.
- 요소는 한 방향으로만 검색한다.
- 재설정(reset)할 수 없으므로 한 번만 반복할 수 있다.

## sequence

코틀린의 Collection 연산은 즉시(eager) 발생한다. Sequence 연산은 지연(lazy) 처리된다. 그렇다. 자바의 Stream과 매우 유사하다.

가령, Sequence의 경우 `first()`와 같은 메서드로 원하는 원소를 찾은 즉시 종료할 수 있다.

## 람다 전달

아래는 동일한 문장이다.

```kotlin
postponComputation(1000, object : Runnable { 
    override fun run() { 
        println(42) 
    }
})

postponComputation(1000) { println(42) }  
```

argument가 여러 개일 경우 아래와 같이 사용할 수 있다.

```kotlin
val sumUsingReduce = numbers.reduce {total, num -> 
    total + num
}
```

#### with

with을 사용하여 중복된 변수명을 제거할 수 있다.

```kotlin
fun alphabet(): String {
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {
        for (letter in 'A'..'Z') {
            this.append(letter)
        }
        append("\\nNow I know the alphabet!")
        this.toString()
    }
}
```

this를 사용해 수신 객체에 접근할 수 있다.

만약 수신 객체 자체를 반환해야 한다면 **apply**를 사용한다.

apply를 활용한 buildString과 같은 표준 라이브러리 함수도 지원한다.

```kotlin
fun alphabet() = buildString {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\\nNow I know the alphabet!")
}
```

## 컬렉션

코틀린의 Collection은 일기 전용 라이브러리이디. 쓰기를 수행하려면 MutableCollection을 사용해야 한다. 

![img_2.png](img_2.png)

## 리스트

`val strings = listOf("a", "b", "c")`와 같이 리스트를 생성할 수 있다.

## 산술 연산자 오버로딩

코틀린에서 가장 신선했던 기능 중 한가지이다.

`operator fun`을 사용하면 특정 클래스에 `+`, `*`, `/`, `%`, `-`의 연산에 대해 메서드 오버로딩으로 정의할 수 있다.

```kotlin
data class Money(
    val value: Int
) {
    operator fun plus(money: Money): Money = Money(value + money.value)
}
```

```kotlin
val money1 = Money(1000)
val money2 = Money(500)
val sum = money1 + money2
```

아래와 같은 단항 연산자를 오버로딩할 수 있는 메서드도 있다.
- unaryPlus
- unaryMinus
- not
- inc
- dec

#### equals

코틀린은 `==`, `!=` 연산자를 호출할 때 `quals()`를 호출한다.

그래서 equals를 재정의하면 동등식을 정의할 수 있다.

#### 범위

in, until 구문으 사용하면 특정 범위에 해당하는 지를 알 수 있다.

```kotlin
operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x until lowerRight.x &&
           p.y in upperLeft.y until lowerRight.y
}
```

특정 범위의 반복문은 아래와 같이 수행할 수 있다.

```kotlin
(0..n).forEach { print(it) }
```

#### 구조 분해

구조 분해를 사용하면 복잡한 값을 여러 변수에 할당할 수 있따.

```kotlin
data class Point(val x: Int, val y: Int)
fun main(args: Array<String>) {
    val p = Point(10, 20)
    val (x, y) = p
}
```

## 함수

코틀린의 함수는 파라미터의 default 값을 지정할 수 있다.

```kotlin
fun setUserInfo(email: String, phone: String = "000-0000-0000") {
    this.email = email
    this.phone = phone
}
```

```kotlin
setUserInfo("violetbeach@gmail.com")
```

## use

자원 관리를 위해 인라인된 try-with-resource 대신 use를 사용할 수 있다.

```kotlin
fun readFirstLineFromFile(path: String): String {
    BufferedReader(FileReader(path)).use { br ->
        return br.readLine()
    }
}
```

use는 Closable 클래스에 대해 람다를 호출한 다음 close 처리한다.

## label

코틀린의 모든 표현식은 @을 이용하여 label 될 수 있다.

아래 코드는 가장 바깥의 반복문을 break로 제어하는 것이다.
```kotlin
loop@ for (i in 1..100) {
    for (j in 1..100) {
        if (...) break@loop
    }
}
```

아래와 같이 반복문에 대해서 반환할 수도 있따.

```kotlin
fun foo1() {
    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit // 람다식(forEach loop)에 대한 local return
        print(it)
    }
    print("명시적 label을 사용한 코드")
}

fun foo2() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach // 람다식(forEach loop)에 대한 local return
        print(it)
    }
    print("묵시적 label을 사용한 코드")
}
```

## 제너릭

코틀린에서 제너릭의 상한이 없다면 `Any?`로 한다.

```kotlin
class Processor<T> {
    fun process(value: T) { 
        value.hashCode() // "value"는 널일 수 있으므로 CompileError! 
    }
}
```

#### 변성

자바와 마찬가지로 List<Any>타입의 파라미터에 List<String>을 넘길 수 없다.

이는 제너릭에는 공변이 없기때문이다.

다만, 아래와 같이 T를 공변으로 선언할 수 있다. (자바의 `T extends User`과 유사하다.)
```kotlin
interface Producer<out T> {  // 클래스가 T에 대해 공변적이라고 선언한다. 
    fun produce(): T
}
```

`out`을 사용하는 공변 클래스는 read는 가능하지만, write는 불가능하다.


`in`은 반공변적으로 만들 수 있다.

#### 스타 프로젝션

타입 인자 대신 `*`를 사용하면 모든 타입을 허용할 수 있다.

```kotlin
fun printFirst(list: List<*>) { 
    if (list.isNotEmpty()) { 
        println(list.first()) 
    }
}
```

## 애노테이션

코틀린의 애노테이션에서 배열을 인자로 받는 경우 `arrayOf`를 사용해도 되고, 가변 길이 인자의 경우 `"abc", "def"`와 같이 나열할 수 있다.

코틀린 1.2부터는 아래와 같이 `[]`문법도 지원한다.

```kotlin

@RequestMapping(value = ["v1", "v2"], path = ["path", "to", "resource"])
```

## 직렬화

코틀린의 JSON 직렬화 라이브러리도 어떤 객체든 Json으로 변환할 수 있어야 한다.

직렬화 라이브러리에서 실행 시점 전에 직렬화할 프로퍼티나 클래스 정보를 알 수 없으므로 리플렉션을 사용해야 한다.

코틀린에서는 자바가 제공하는 `java.lang.reflect`와 코틀린이 제공하는 `kotlin.reflect` 패키지 중 한 가지를 선택해서 사용할 수 있다.

#### 클래스 인자

자바에서는 `.class`를 사용했지만, 코틀린에서는 `::class`를 사용한다.

코틀린에서 클래스를 표현하는 클래스는 `KClass`이다.

## TypeAlias

아래와 같이 특정 타입에 대해 Alias를 붙일 수 있다.

```kotlin
typealias Args = Array<String>
fun main(args:Args) { }

typealias StringKeyMap<V> = Map<String, V>
val myMap: StringKeyMap<Int> = mapOf("One" to 1, "Two" to 2)
```

## JvmField / JvmStatic

#### JvmField

코틀린의 @JvmField 애노테이션은 코틀린 컴파일러가 자동으로 getter / setter를 생성하지 못하도록 막는다.

#### JvmStatic

코틀린의 companion object는 자반의 static과 거의 유사하지만, 실제로는 다르다.

아래 코틀린 코드를 자바 코드로 변환해보자.
```kotlin
class Bar {
    companion object {
        var barSize : Int = 0
    }
}
```

```java
public final class Bar {
   private static int barSize;
   public static final class Companion {
      public final int getBarSize() {
         return Bar.barSize;
      }
      public final void setBarSize(int var1) {
         Bar.barSize = var1;
      }
   }
}
```

즉, Companion 클래스에 getter, setter가 내장된 형식으로 변환된다.

코틀린의 `@JvmStatic`를 사용하면 아래와 같이 Companion 클래스를 생성하지 않는다.

```java
public final class Bar {
   private static int barSize;
   public static final int getBarSize() {
      return barSize;
   }

   public static final void setBarSize(int var0) {
      barSize = var0;
   }

   public static final class Companion {
      public final int getBarSize() {
         return Bar.barSize;
      }
      public final void setBarSize(int var1) {
         Bar.barSize = var1;
      }
   }
}
```

근데 이게 실제로는 차이가 없지 않느냐..? 라고 생각한다면, 자바에서 해당 코틀린 코드를 사용할 때의 문제 때문에 `@JvmStatic`을 활용한다.

## 마무리

해당 게시글은 실무에 빠른 투입을 위해 문법에 대해 **빠르게** 학습할 목적으로 정리한 것이다.

정확하지 않거나 부적절한 내용이 있을ㅇ 수 있고, 당연히 븝로그에 올릴 생각도 없다.

## 참고
- https://0391kjy.tistory.com/57
- https://kotlinlang.org/docs/jvm-get-started.html
- https://incheol-jung.gitbook.io/docs/study/kotlin-in-action/1
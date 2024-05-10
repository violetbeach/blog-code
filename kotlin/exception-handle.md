## mapCatching

코틀린의 Scope function에서 예외가 터질 경우 핸들링하는 방법을 알아보자. 

`runCatching {}`과 동일하게 `mapCatching {}`을 사용하면 해당 블록내에서 Exception이 발생하더라도 핸들링할 수 있다.

```kotlin
var result = runCatching { "테스트" }
    .mapCatching { throw Exception("예외") }
    .getOrDefault("기본 값")
```

## recover

```kotlin
var result = runCatching { "테스트" }
    .recover { "복구" }
    .getOrNull()
```

recover는 성공 시 동작하지 않고, 실패 시 동작을 하게 된다.

## recoverCatching

`recoverCatching {}`을 사용하면 `mapCatching`과 마찬가지로 함수 내에서 예외가 발생하더라도 복구할 수 있다.

```kotlin
var result = runCatching { "테스트" }
    .recover { "복구" }
    .getOrNull()
```
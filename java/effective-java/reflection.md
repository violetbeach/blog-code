# Java - 리플렉션 사용하기

리플렉션이 구체적인 클래스 타입을 알지 못해도, 해당 클래스의 메소드나 타입, 변수 들에 접근할 수 있도록 해주는 자바 API이다.

리플렉션은 JVM이 클래스 로더를 통해 읽어온 클래스 정보(reflect된 정보)를 통해 접근한다.

리플렉션은 아래의 경우 사용한다.
- 특정 애노테이션이 붙어있 필드 또는 메소드를 읽어오기
- 특정 이름 패턴에 해당하는 메소드 목록 가져와 호출하기
- ...

## 예시
리플렉션을 사용하면 문자열로 클래스를 만들 수 있다. 아래의 코드를 보자.
```java
Class<?> HelloClass = Class.forName("jaehoney.blogcode.Hello");
```
문자열(package name)를 사용해서 클래스 정보를 꺼내온다.

이후에 해당 클래스의 인스턴스를 만들 수도 있다.
```java
Constructor<?> constructor = helloClass.getConstructor();
Hello hello = (Hello) constructor.newInstance();
```
해당 클래스로 예전에는 `Class.newInstance()` 메서드로 인스턴스를 만들 수 있었지만, 해당 방법은 현재는 권장되지 않는다.(deprecated)

현재는 생성자를 리플렉션으로 조회한 후 해당 생성자를 사용해서 인스턴스를 생성하는 방법이 권장된다.

이외에도 Class 인스턴스에는 아래의 메서드를 사용할 수 있다.
- getDeclaredMethod(String name, Class<?>... parameters) -> 해당 클래스의 메서드를 조회한다.
- getDeclaredMethods -> 해당 클래스의 모든 정의된 메서드를 조회한다.
- getDeclaredField -> 해당 클래스에 정의된 필드를 조회한다.
- getDeclaredAnnotations -> 해당 클래스의 애노테이션을 조회한다.

이러한 기능들을 통해서 해당 클래스가 가지고 있는 필드의 값을 조회하고 변경하는 것이 가능하다.

심지어 해당 필드나 메서드의 접근제어자가 private이라도 가능하다.

## 참고
- https://docs.oracle.com/javase/tutorial/reflect
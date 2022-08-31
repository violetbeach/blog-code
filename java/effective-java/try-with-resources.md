# Effective Java - try finally 대신 try with resources 사용하기!

## 기존의 코드 - try, finally

Java 7 버전 이전에는 다 사용하고 난 자원을 반납하기 위해 try-catch-finally 구문을 사용했다.

예시로 살펴보자.
```java
public class MainApplication {
    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("input.txt"));
            // scanner 사용
        } catch (FileNotFoundException e) {
            // 예외 처리
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
```
예시에서는 Scanner를 생성하고 finally 구문 안에서 자원을 해제해주고 있다.

해당 방법은 리소스의 반납을 추가해야하는 수고로움이 필요하고, 실수로 빼먹을 수도 있다.

이러한 문제 때문에 Java 7 버전부터는 **try with resources**라는 기능을 제공한다.

## 변경된 코드 - try with resources
try with resources를 사용한 예제를 살펴보자.
```java
public class MainApplication {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(new File("input.txt"))) {
            // scanner 사용
        } catch (FileNotFoundException e) {
            // 예외 처리
        }
    }
}
```
코드 길이가 줄어들 뿐 아니라, finally에서 자원 할당을 해제하지 않아도 try를 벗어나면 자동으로 자원을 반납한다.

다만 try문 안에 있는 객체는 타입이 `java.lang.AutoCloseable`을 구현한 타입이어야 한다. (예제의 Scanner도  AutoCloseable 인터페이스를 구현하고 있다.)

## 참고
- https://hianna.tistory.com/546
객체 지향 설계 5원칙 SOLID에 대해 알아보자.

## SRP: Single Responsibility Principle 단일 책임 원칙

### Bad

아래 클래스가 있다.
```java
class UserData {
    String id;
    String username;
}
```
그리고 아래와 같이 `UserData` 클래스를 무분별하게 사용한다.

```java
class UserController {
    UserData getUser() {}
    void postUser(UserData userData) {}
}

class UserService {
    UserData getUser() {}
}
```

여기서 패스워드 변경 기능이 추가되면 `UserData`에 `password` 필드를 추가해야 한다.

```java
class UserData {
    String id;
    String username;
    String password;
}
```

조회 응답에서도 노출되어선 안되는 `password`를 내려주게 될 수 있다.

즉, 특정 클래스의 변경이 수많은 클래스로 전파된다. 추가로 가독성도 좋지 않다.

### Good

```java
class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
}

class UserService {
    void changePassword(ChangePasswordRequest request) {}
}
``` 

`UserData`라는 DTO를 무법자처럼 사용하는 것이 아니라 `ChangePasswordRequest` 같이 책임이 명확한 클래스를 사용한다면 변경이 전파되는 범위가 축소된다. 가독성도 향상된다.

## OCP: Open-Closed Principle 개방-폐쇄 원칙

아래 클래스는 **도형의 넓이**를 계산하는 클래스이다.

### Bad

```java
class AreaCalculator {
    double calculateArea(Shape shape) {
        if (shape.getType().equals("Circle")) {
            return Math.PI * Math.pow(radius, 2);
        } else if (shape.getType().equals("Rectangle")) {
            return width * height;
        }
        return 0;
    }
}
```

각 도형은 Shape 인터페이스를 구현하고 있다.
```java
class Shape {
    String type;

    Shape(String type) {
        this.type = type;
    }

    String getType() {
        return type;
    }
}

class Circle extends Shape {
    Circle() {
        super("Circle");
    }
}

class Rectangle extends Shape {
    Rectangle() {
        super("Rectangle");
    }
}
```

이때 새로운 도형이 추가되면 `AreaCalculator`의 `calculateArea(Shape shape)` 메서드를 변경해줘야 한다.

다시 아래 코드를 보자.

### Good

```java
interface Shape {
    double calculateArea();
}

class Circle implements Shape {
    private double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * Math.pow(radius, 2);
    }
}

class Rectangle implements Shape {
    private double width;
    private double height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return width * height;
    }
}

class AreaCalculator {
    double calculateArea(Shape shape) {
        return shape.calculateArea();
    }
}
```

이제 새로운 도형 클래스가 추가될 때 기존의 코드를 변경하지 않아도 된다.

**확장에는 열려있지만, 변경에는 닫혀있는 것**이다.

이것이 개방 폐쇄 원칙이다.

## LSP: Liskov Substitution Principle 리스코프 치환 원칙

### Bad

```java
class Bird {
    
    public void cry() {
        System.out.println("cry!");
    }
    
    public void fly() {
        Sky.count++;
    }
}

class Sparrow extends Bird {
    
    @Override
    public void fly() {
        super.fly();
        System.out.println("Sparrow fly!");
    }
    
}
```

만약 위 코드에서  `cry()`를 재사용하기 위해 Dog 클래스도 추가했다.

```java
class Dog extends Bird {
    @Override
    public void fly() {
        return;
    }
}
```


`fly()`는 사용하면 안되므로 상속된 메서드를 호출하지 못하도록 바로 `return`한다.

```java
class Sky {
    
    static int count = 0;
    
    void printlCount(Bird bird) {
        bird.fly();
        System.out.println(count);
    }
}
```

위 코드의 문제는 `bird` 변수로 `Dog`가 들어올 수 있다는 점이다.

`Dog`의 `fly()`는 `Sky.count`를 증가시키지 않지만, 개발자는 그렇게 생각하지 않는다. `Bird`가 `fly()`를 호출했으니까 count가 하나 증가되었을 거라고 생각할 가능성이 높다.

즉, 해당 코드는 혼란을 야기할 수 있으며 장애 가능성이 높은 코드가 된다.

### Good

```java
class Animal {
    public void cry() {
        System.out.println("cry!");
    }
}

class Bird {
    public void fly() {
        Sky.count++;
    }
}

class Sparrow extends Bird {
    @Override
    public void fly() {
        super.fly();
        System.out.println("Sparrow fly!");
    }
}

class Dog extends Animal {
}
```

위와 같이 코드를 작성한다면 사용자 측 혼란이 없을 것이고, `Dog`의 구현체로 `fly()`를 호출하는 일도 사라진다.

즉, 장애 가능성이 사라지고 이해 가능한 코드가 된 것이다.

리스코프 치환 원칙은 **자식 객체**가 **부모 객체의 역할을 완전히 수행**할 수 있어야 한다는 것을 의미한다.

## ISP: Interface Segregation Principle 인터페이스 분리 원칙

### Bad

```java
class BankService {
    Money getBalance() {}
    void transfer() {}
}
```

위와 같이 `MoneyService`가 있다고 가정하자. `AccountService`에서는 **자신의 프로필에서 잔금을 조회**할 수 있어야한다.

그래서 아래와 같이 `MoneyService`의 `getBalance()`를 호출했다.

```java
class AccountService {
    MoneyService moneyService;
    
    Profile getProfile() {
        moneyService.getBalance();
        // ...
    }
}
```

문제는 여기서 **넓은 인터페이스(클래스) 문제**가 발생한다. `AccountService`는 **잔금 조회**만 알고 싶은데 **송금**까지 알아버린 것이다.

여기서 개발자가 실수로 `transfer()`를 호출할 기회가 열리게 된다.

넓은 인터페이스 문제는 테스트도 어렵게 만든다. `AccountService`를 테스트하려면 `MoneyService`의 사용하지 않을 메서드까지도 모킹해야 한다.

### Good

```java
interface BalanceUseCase {
    Money getBalance();
}

interface TransferUseCase {
    void transfer();
}

class BankService extends BalanceUseCase, TransferUseCase {
    Money getBalance() {}
    void transfer() {}
}
```

위와 같이 인터페이스를 분리한다면 `AccountService`를 아래와 같이 구현할 수 있다.

```java
class AccountService {
    BalanceUseCase balanceUseCase;
    
    Profile getProfile() {
        balanceUseCase.getBalance();
        // ...
    }
}
```

이제 `AccountService`가 `transfer()`를 몰라도 된다. 즉, 안전한 코드가 되었으며 테스트가 용이해졌다.

## DIP: Dependency Inversion Principle 의존 역전 원칙

### Bad

아래 코드는 주문을 담당하는 `OrderService`의 예시이다. 주문이 끝나면 외부 라이브러리를 통해 내용을 출력한다.

```java
class OrderService {
    String id;
    AAALibraryPrinter printer;
    
    void order() {
        printer.goodPrint("주문이 완료되었습니다.");
    }
}
```

문제는 뭘까..? `OrderService`가 `AAALibraryPrinter`에 의존한다는 점이다.

라이브러리를 바꾸면 메인 코드로 변경이 전파된다. 만약 Printer를 `BBBLibraryPrinter`로 바꾸면 필드 타입도 변경해줘야 하고 메서드도 `goodPrint()`가 아니라 `preetyPrint()`로 변경된다.

즉, **안정적이어야 하는 도메인 코드**가 **변경에 취약한 코드**가 된다.

### Good

```java
class OrderService {
    String id;
    Printer printer;
    
    void order() {
        printer.print("주문이 완료되었습니다.");
    }
}

interface Printer {
    void print();
}

class PrinterImpl implements Printer {

    AAALibraryPrinter printer;
    
    void print() {
        printer.goodPrint("주문이 완료되었습니다.");
    }
}
```

위와 같이 분류한다면 안정적인 도메인 로직인 `OrderService`가 **Printer의 내부 로직이 아닌** **결과를 출력해야 한다는 요구사항(도메인)** 에 의존할 수 있다.

의존 역전 원칙은 자세히 설명하면 **저수준 모듈이 고수준 모듈에 의존해야 한다는 것**이다. 더 쉽게 설명하면 안정적인 것이 불안정적인 것에 의존하지 말고, 불안정적인 것이 안정적인 것에 의존해야 한다.

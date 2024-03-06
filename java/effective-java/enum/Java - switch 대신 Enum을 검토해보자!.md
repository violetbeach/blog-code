아래 내용은 Effective Java 내용에 기반한다.

## 상수 대신 Enum

아래 코드를 보자.

```java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;
public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```

이 코드의 영향은 어떤 것이 있을까..?

- 상수의 값이 바뀌면 반드시 다시 컴파일해야 한다.
- 추적이 어렵다. (0, 1, 2로 저장되니까)
  - 추적이 어렵다는 이유로 int 대신 String을 사용한다면 추적은 쉬워지겠지만 불안정한 시스템이 된다.
- 분기를 수행하기 어렵다.
- 순회하기 어렵다.
- 상수의 이름을 중복할 수 없다.

반면 아래 예시를 보자.

```java
public enum Apple {FUJI, PIPPIN, GRANNY_SMITH}
public enum Orange {NAVEL, TEMPLE, BLOOD}
```

여기서는 컴파일 타입 안정성이 제공된다.

APPLE 값으로 다른 값이 들어오면 컴파일 에러가 난다.

Enum은 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다.

## 상수 내재

Enum은 특정 데이터와 연관지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.

Enum의 생성자는 해당 클래스 내부적으로만 동작하게 된다.

```java
@Getter
public enum Planet {
     MERCURY(3.302e+23, 2.439e6),
     VENUS(4.869e+24, 6.052e6),
     EARTH(5.975e+24, 6.378e6),
     MARS(6.419e+23, 3.393e6),
     JUPITER(1.899e+27, 7.149e7),
     SATURN(5.685e+26, 6.027e7),
     URANUS(8.683e+25, 2.556e7),
     NEPTUNE(1.024e+26, 2.447e7);
     
     private final double mass;                // 질량(단위: 킬로그램)
     private final double radius;             // 반지름(단위: 미터)
     private final double surfaceGravity;  // 표면중력(단위: m / s^2)
     
     // 중력상수 (단위: m^3 / kg s^2)
     private static final double G = 6.67300E-11;
     
     Planet(double mass, double radius) {
          this.mass = mass;
          this.radius = radius;
          this.surfaceGravity = G * mass / (radius * radius);
     }
     
     public double surfaceWeight(double mass) {
          return mass * surfaceGravity;
     }
}
```

해당 클래스를 사용하면 어떤 행성의 지구에서의 무게를 입력받아, 해당 행성에서의 지구의 무게를 출력하는 것도 간단히 가능하다.

```java
public classs WeightTable {
     public static void main(String[] args) {
          double earthWeight = Double.parseDouble(args[0]);
          double mass = earthWeight / Planet.EARTH.surfaceGravity();
          for (Planet p : Palanet.values()) 
                System.out.println("%s에서 무게는 %f이다. %n", p, p.surfaceWeight(mass));
     }
}
```

#### Enum의 기본 메서드

참고로 위에서 몇가지 Enum의 메서드를 사용했다. 지원하는 메서드는 아래와 같다.
- values(): Enum에 정의된 상수들을 배열에 담아 반환
- valueOf(): 상수 이름을 입력받아 해당 상수를 반환
- toString(): 상수 이름을 문자열로 반환
- fromString(): `toString()`이 반환하는 문자열을 해당 열거 타입 상수로 반환

이러한 메서드는 재정의도 가능하기에 다양하게 활용할 수 있다.

## switch 대신 Enum

만약 상수별로 동작이 다른 경우는 아래와 같이 구현할 수 있을 것이다.

```java
public enum Operation {
     PLUS, MINUS, TIMES, DIVIDE
}

public double apply(double x, double y) {
     switch(this) {
          case PLUS: return x + y;
          case MINUS: return x - y;
          case TIMES: return x * y;
          case DIVIDE: return x / y;
     }
     throw new AssertionError("알 수 없는 연산: " + this);
}
```

이 코드는 아쉽게도 문제점이 여러가지 있다.
- 연산이 추가될때마다 비즈니스 코드인 `apply()`를 수정해야 한다.
- 기술적으로 case에 도달할 수 있기 때문에 default 값이나 throw가 강제된다.
- 실수로 case를 추가하지 않으면 에러가 발생한다.

이 문제를 추상 메서드를 사용하면 유용하게 해결할 수 있다. 아래의 각 열거형 요소는 추상 메서드를 구현한다.

```java
public enum Operation {
     PLUS {public double apply(double x, double y) {return x + y;}},
     MINUS {public double apply(double x, double y) {return x + y;}},
     TIMES {public double apply(double x, double y) {return x + y;}},
     DIVIDE {public double apply(double x, double y) {return x + y;}};

     public abstract double apply(double x, double y);
}
```

다른 방법으로는 함수형 인터페이스를 사용할 수도 있다.

```java
public enum Operation {
     PLUS((x, y) -> x + y),
     MINUS((x, y) -> x - y),
     TIMES((x, y) -> x * y),
     DIVIDE((x, y) -> x / y);

     private final DoubleBinaryOperator operator;

     Operation(DoubleBinaryOperator operator) {
          this.operator = operator;
     }
}
```

나머지 문제가 있다. 입력으로 들어온 `+`, `*`, `-`, `/`를 Enum으로 변환해야 한다.

```java
public enum Operation {
      PLUS('+', (x, y) -> x + y),
      MINUS('-', (x, y) -> x - y),
      TIMES('*', (x, y) -> x * y),
      DIVIDE('/', (x, y) -> x / y);

      private final char symbol;
      private final DoubleBinaryOperator operator;

      Operation(char symbol, DoubleBinaryOperator operator) {
            this.symbol = symbol;
            this.operator = operator;
      }

      public double apply(double x, double y) {
            return operator.applyAsDouble(x, y);
      }

      public char getSymbol() {
            return symbol;
      }

      private static final Map<String, Operation> stringToEnum = 
                 Stream.of(values()).collect(toMap(Object::toString, e -> e));

      public static Optional<Operation> fromString(String symbol) {
            return Optional.ofNullable(stringToEnum.get(symbol));
      }
}
```

위와 같이 symbol 필드를 추가하고, `fromString()`를 구현해주면 된다. 여기서는 Invalid 값이 들어왔을 때 예외를 터트리기보다는 상위 모듈에서 처리가 가능하도록 `Optional`을 사용했다.

### 마무리

Enum은 상수보다 뛰어나며 더 읽기 쉽고 강력하다. Enum을 활용하면 생각보다 많은 것을 할 수 있다.

Enum 요소의 필드로 다른 타입의 Enum 요소를 가질 수 있고 이를 활용한다면 아래와 같은 전략 열거 패턴을 사용할 수 있다.

```java
enum PayrollDay {
     MONDAY, TUESDAY, WEDSDAY, THURSDAY, FRIDAY, 
     SATURDAY(PayTyoe.WEEKEND), SUNDAY(PayType.WEEKEND);
     
     private final PayType payType;
     
     PayrollDya(PayType payTyoe) {this.payType = payType;}
     
     int pay(int minutesWorked, int payRate) {
         return payType.pay(minutesWorked, payRate);
     }
     
     enum PayType {
          WEEKDAY {
                int overtimePay(int minusWorked, int payRate) {
                     return minusWorked <= MINS_PER_SHIFT ? 0 :
                     (minusWorked - MINS_PER_SHIFT) * payRate / 2;
                }
          },
          WEEKEND {
                int overtimePay(int minusWorked, int payRate) {
                     return minusWorked * payRate / 2;
                }
          };
          
          abstract int overtimePay(int mins, int payRate);
          private static final int MINS_PER_SHIFT = 8 * 60;
          
          int pay(int minsWorked, int payRate) {
                int basePay = minsWorked * payRate;
                return basePay + overtimePay(minsWorked, payRate);
          }
     }
}
```

Enum이 매우 유용하긴 하나 당연히 남용해서는 안된다. Effective Java에서는 Enum을 사용하는 기준을 **컴파일 중에 필요한 원소를 모두 알 수 있는 상수의 집합**이라면 Enum을 사용할 것을 권고한다.

상수가 추가되거나 제거되는 경우 사용하지 말아야 된다는 말이 아니다. Enum은 바이너리 수준에서 요소의 추가, 제거를 지원한다.





## 참고

- https://velog.io/@new_wisdom/Effective-java-item-34.-int-상수-대신-열거-타입을-사용하라
- https://www.yes24.com/Product/Goods/65551284
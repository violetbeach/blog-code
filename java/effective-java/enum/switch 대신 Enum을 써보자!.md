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
    
    private final double mass;            // 질량(단위: 킬로그램)
    private final double radius;          // 반지름(단위: 미터)
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

#### Enum의 메서드

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



## 참고

- https://velog.io/@new_wisdom/Effective-java-item-34.-int-%EC%83%81%EC%88%98-%EB%8C%80%EC%8B%A0-%EC%97%B4%EA%B1%B0-%ED%83%80%EC%9E%85%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC
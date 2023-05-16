## 객체지향 생활 체조 원칙 9가지 익히기!

최근에 코드 리뷰를 <code>객체지향 생활 체조 원칙</code>를 기반으로 한다는 얘기를 처음 접했다.

객체지향 생활 체조 원칙에 대해 살펴봤는데, 기존에 오브젝트라는 책과 DDD 관련 책에서 적혀있던 내용과 더불어서 좋은 개념들이 많았다!

그래서 이번 포스팅에서는 객체지향 생활 체조에 대해서 작성한다.

## 객체지향 생활 체조 원칙

객체지향 생활 체조 원칙은 소트웍스 앤솔러지(ThoughtWorks Anthology)라는 책에 나오는 원칙이다.

아래의 9가지 원칙을 준수하면서 객체지향을 추구할 수 있다고 한다.
1. 한 메서드에 오직 한 단계의 들여쓰기(indent)만 한다.
2. else 예약어를 쓰지 않는다.
3. 모든 원시 값과 문자열을 포장한다.
4. 한 줄에 점을 하나만 찍는다.
5. 줄여 쓰지 않는다(축약 금지).
6. 모든 엔티티를 작게 유지한다.
7. 3개 이상의 인스턴스 변수를 가진 클래스를 쓰지 않는다.
8. 일급 컬렉션을 쓴다.
9. getter/setter/프로퍼티를 쓰지 않는다.

## 한 메서드에 오직 한 단계의 들여쓰기(indent)만 한다.

한 메서드 if/for/while 문 등을 2 depth 이상 사용하지 않는다.

해당 부분만 지켜도 가독성이 향상되고, 메서드가 자연스럽게 분리되는 효과가 있다.

#### 기존 코드

```java
public static int operate(int[] numArray, String op) {
    int result = 0;
    if(op.equuals("+")) {
        for(int i = 0; i < numArray.length; i++) {
            result += numArray[i];
        }
        return result;
    }
    if(op.equals("*")) {
        // ..생략
    }
}
```

#### 수정 코드

```java
public static int operate(int[] numArray, String op) {
    if(op.equals("+")) {
        return sum(numArray);
    }
    // 생략
}

private static int sum(int[] numArray) {
    int result = 0;
    for(int i = 0; i < numArray.length; i++) {
        result += numArray[i];
    }
    return result;
}
```

## 2. else 예약어를 쓰지 않는다.

else가 있는 코드는 의도를 파악하기 어렵다. ealry return을 통해 의도를 분명히 나타낼 수 있다.


#### 기존 코드

```java
public static int operate(int[] numArray, String op) {
    int result = 0;
    if(op.equals("+")) {
        result = sum(numArray);
    } else {
        result = minus(numArray);
    }
    return result;
}
```

#### 수정 코드

```java
public static int operate(int[] numArray, String op) {
    if(op.equals("+")) {
        return sum(numArray);
    } 
    return minus(numArray);
}
```

예외 상황의 경우 ealry return으로 인해 흐름에서 나가므로 유지보수가 쉬워진다는 장점도 있다.


## 더 알아보고 싶으면

자바지기 박재성님께서 진행하신 TDD 강연에서도 객체지향 생활 체조 원칙을 소개하고 있습니다.
- https://www.youtube.com/watch?v=bIeqAlmNRrA

관심이 있으시면 참고하시면 좋을 듯합니다.
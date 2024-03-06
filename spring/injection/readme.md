## Spring - 의존성 주입의 3가지 방법 비교!

의존성을 주입하는 방법은 총 3가지가 있다.
- Field Injection
- Setter based Injection
- Constructor based Injection

사실 생성자 주입이 좋다고는 다들 알고 있을 것이다. 그렇다면 왜 생성자 주입이 좋을까?

(자주 깜빡해서 깔끔하게 정리하고자 포스팅한다.)

## Field Injection

Field Injection은 의존성을 주입하고자 하는 필드에 @Autowired 애노테이션을 붙이면 된다.

```java
@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;
    
}
```

필드 주입 방식은 아래와 같은 절차로 동작한다.
1. 주입받으려는 빈의 생성자를 호출하여 빈을 찾거나 빈 팩토리에 등록
2. 생성자 인자에 사용하는 빈을 찾거나 만듬
3. 실제로 해당 필드를 사용할 때 필드에 주입

해당 방식의 문제는 빈이 일단 생성이 가능하다는 것이다.

즉, 필드에 의존을 주입하지 않아도 스프링에서 에러를 뱉지 않는다.

그래서 실제 환경에서 앱을 실행시킨 이후 memberService에 접근할 때 비로소 NPE가 발생하게 된다.

## Setter based Injection

Setter 메서드에 @Autowired 어노테이션을 붙여 의존성을 주입하는 방식이다.

```java
@RestController
public class MemberController {

    private MemberService MemberService;
    
    @Autowired
    public void setMemberService(MemberService MemberService){
        this.MemberService = MemberService;
    }
    
}
```

필드 주입 방식과 과정이 유사하며, 다른 점은 필드에 바로 주입하는 것이 아니라 주입하려는 빈 객체의 수정자(Setter)를 호출하여 주입을 한다는 점이다.

해당 방식도 필드 주입 방식과 동일한 문제가 있다.

주입할 빈이 없어도 이미 빈이 생성되고 에러 처리가 되지 않는다.

그래서 memberController에서 memberService에 접근할 때 비로소 NPE가 발생한다.



## Constructor based Injection

생성자를 사용해서 의존성을 주입하는 방식이다.

```java
@RestController
public class MemberController {

    private final MemberService MemberService;
    
    public MemberController(MemberService MemberService){
        this.MemberService = MemberService;
    }
    
}
```

해당 방식은 객체가 생성되는 시점에 빈을 주입하기 때문에 주입할 빈이 없다면 에러가 터진다.

즉, 앱 구동 시점에 에러를 확인할 수 있다.

## 추가

가장 큰 이유는 위에서 언급한 주입할 빈이 없을 때에 대한 에러 처리일 것이다.

하지만 꼭 주입할 빈이 없는 문제만 고려한 것은 아니다.

예를 들어 아래의 경우를 생각해보자.
- A 클래스의 a 메서드는 B 클래스의 b 메서드를 호출한다.
- B 클래스의 b 메서드는 A 클래스의 a 메서드를 호출한다.

이때 필드 주입 방식을 사용하면 A 빈과 B 빈에 서로의 빈 주입이 모두 정상적으로 완료가 된다!
- 이후 A 빈의 a 메서드 혹은 B 빈의 b 메서드를 호출할 때 순환참조가 발생하므로 StackOverFlowError가 나면서 앱이 꺼진다.

생성자 주입 방식을 사용하면 A 클래스가 B에 의존하고, B 클래스가 A에 의존하면 애초에 빈이 생성되지도 않고 에러를 발생한다.
- 즉, 잘못된 설계를 사전에 방지할 수 있다는 장점이 있다.

(이런 잘못된 설계는 SonarQube나 ArchUnit 등을 사용해서 체크할 수도 있다.)

추가로 생성자 주입 방식은 final 키워드를 사용해서 불변성(Immutable)의 장점을 살리고 Null이 아님을 보장할 수 있다.

## 참고
- https://jgrammer.tistory.com/entry/springboot-%EC%9D%98%EC%A1%B4%EC%84%B1-%EC%A3%BC%EC%9E%85-%EB%B0%A9%EC%8B%9D-%EA%B2%B0%EC%A0%95




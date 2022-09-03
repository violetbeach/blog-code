# 테스트 코드가 실제 코드에 영향을 줘선 안되는가 ?

많은 분들이 테스트 코드가 프로덕트 코드에 영향을 줘선 안된다고 알고 있을 것이다.

나 조차도 내가 개발한 코드는 절대 테스트 코드가 프로덕트 코드에 영향을 주지 않고 있다고 믿었다.

그러다가 회사 팀 동료 A에게 이러한 질문을 들었다.
A. Jerry! 서비스 테스트 코드를 작성할 떄 RequestDto가 필요한데, 해당 Dto는 getter만 열어뒀거든요. 서비스 테스트하려면 Builder나 Constructor를 만들어줘야 하나요??

Jerry(본인): (3분 정도 고민하다가..) 헐헐 그러네요!! 저는 테스트가 프로덕트 코드에 영향이 절대 없게 개발하는 것을 목표로 했었는데, 따지고 보면 생성자 조차도 필요 없는 것이네요..!

완전 충격을 받았다.

## 상황
해당 RequestDto는 아래와 같았다.
```java
public class MessageDto {
    
    @Getter
    @NoArgsConstructor
    public static class Send {
        // 생략
    }
    
}
```
MessageDto.Send는 Controller를 통해 RequestBody로 들어오기 때문에 @NoArgsConstructor가 필요했다.

즉, 위와 같은 클래스로도 프로덕트 코드를 돌리는 데 전혀 문제가 없었다.

문제는 테스트 코드에서 사용해서 데이터를 만들 수 있는 인터페이스가 없었던 것이었다.

### 반성

나는 RequestDto를 만들 때 너무나도 습관적으로 NoArgsConstructor와 필요한 생성자 메서드 1개를 제공했다.

하지만 프로덕트에서는 RequestBody를 통해 데이터가 생성되므로 생성자 메서드 1개는 프로덕트 코드에 필요하지 않았다.

## 고민

테스트 코드를 만들면서 실제 코드가 리팩토링을 거치게 되는 것은 좋은 현상이다.

하지만, 잘 돌아가는 실제 코드에서 테스트를 위해 메서드를 추가한다..라는 것이 정말 맞는 것일까?

좋은 코드라고 믿는 여러가지 소스 코드를 뒤져본 결과, 기본적으로 RequestDto에도 Constructor를 사용해서 인스턴스를 생성해서 테스트를 진행하는 것 같다.

또는 생성자를 열지 않고 Setter를 여는 경우도 볼 수 있었다.
- (Product) https://github.com/microsoft/spring-cloud-playground/blob/devel/src/main/java/com/microsoft/azure/springcloudplayground/generator/ProjectRequest.java
- (Test) https://github.com/microsoft/spring-cloud-playground/blob/devel/src/test/java/com/microsoft/azure/springcloudplayground/SpringCloudPlaygroundApplicationTests.java

## 결론

그래서 테스트 코드가 프로덕트 코드에 절대 영향이 없어야 한다는 것은 반만 맞는 얘기인 것 같다.

프로덕트 코드를 생성할 때 우리는 **필요 없는 코드를 미리 작성하지마라는 원칙(YAGNI 원칙)**을 지키고 있다.

이때 테스트 코드를 위해서 인터페이스(Class, Interface할 때 인터페이스를 말하는 것이 아니다!)를 추가해야 한다면 **1. 그것이 정말 필요하고 2. 의미상 적절하고 3.해당 인터페이스를 열었을 때 발생할 문제가 없다면 추가할 수 있다!**가 내가 내린 결론이다.

그래서 이러한 부분을 생각하게 해준 동료 A에게 감사를 드린다.


> 혹시 다르게 생각하시는 분이 계시다면 피드백은 언제든 환영입니다!!

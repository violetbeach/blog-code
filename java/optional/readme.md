## JAVA - Optional 제대로 사용하는 방법!

## Optional

Optional은 Java 8 버전에 추가된 Null 체크를 대체할 수 있는 방법입니다.

대부분 개발자 분들이 Optional로 인해 Null을 체크하는 등의 코드를 작성하는 방법이 바뀌게 되었는데요..!

여전히 애매한 부분들이 있습니다.

### 애매한 부분

DTO로 SignupRequest를 받아서 회원가입을 한다고 가정해보겠습니다.

그러면 Domain인 User의 생성자에서는 아래와 같이 작성할 수 있을 것 같습니다.

```java
this.name = name != null ? name = defaultName;
```

그런데 Optional을 사용해서 아래와 같이 표현하는 경우도 봤어요..!

```java
this.name = Optional.ofNullable(name).orElse(defaultName);
```

어떤 방법이 맞는 방법일까요..?

알아보겠습니다.

##
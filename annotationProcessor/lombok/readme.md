Lombok의 동작 원리를 이해해보자.

Lombok의 동작 원리를 알려면 먼저 Annotation Processor를 알아야 한다.

## Annotation Processor

Annotation Processor는 자바 컴파일러 플러그인의 일종이다.

애노테이션에 대한 코드를 검사, 수정, 생성하는 훅이다.

예를 들어 특정 애노테이션이 붙어있는 클래스를 특정 기준으로 검사해서 컴파일 에러나 경고륾 만들거나 코드를 조작하는 것도 가능하다.

## 동작 원리

Lombok의 동작 원리를 요약하면 아래와 같다.

1. 자바 컴파일러는 소스 파일을 파싱하여 AST 트리를 만든다.
2. Lombok의 AnnotationProcessor가 AST 트리를 동적으로 수정하고 새 노드를 추가한다.
3. 자바 컴파일러는 AnnotationProcessor에 의해 수정된 AST를 기반으로 바이트 코드를 생성한다.


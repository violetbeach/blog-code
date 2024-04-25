## Mockito

Mockito는 Java에서 가장 많이 사용되는 Mocking Framework이다.

mock, spy, stubbing, verify 등의 기능을 제공한다.

## Mock 객체

Mock 객체란 특정 인터페이스, 클래스의 모든 메서드를 갖는 모의 객체를 말한다.

Stubbing을 통해 주어진 상황에서 특정 액션을 수행하거나 특정 값을 반환할 수 있다.

## MockMaker

내부적으로는 아래와 같이 MockMaker를 사용한다.

```java
public static <T> T createMock(MockCreationSettings<T> settings) {
    MockHandler mockHandler = createMockHandler(settings);

    Object spiedInstance = settings.getSpiedInstance();

    T mock;
    if (spiedInstance != null) {
        ...
    } else {
        mock = mockMaker.createMock(settings, mockHandler);
    }

    return mock;
}
```

MockMaker 인터페이스의 대표적인 구현체는 `InlineByteBuddyMockMaker`, `InlineDelegateByteBuddyMockMaker` 등이 있다.

일반적으로는 `InlineByteBuddyMockMaker`를 사용하는데, ByteBuddy는 ByteCode를 직접적으로 변경해서 특정 메서드의 동작을 Stubbing한다.




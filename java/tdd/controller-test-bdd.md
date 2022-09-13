# JUnit5 - Controller Test에 BDD 적용하기 (+ Rest Docs)

MockMvc를 사용해서 Controller를 Test할 때 BDD를 적용해서 가독성을 향상시키는 방법에 대해 알아보자.

기존의 코드를 살펴보자.

## 기존

기존의 코드는 다음과 같았다.
```java
@Test
@DisplayName("유저를 생성할 수 있다.")
void signup() throws Exception {
    SignUpRequest signUpRequest = SignUpRequest.builder()
            .loginId("test123")
            .password("password12@")
            .name("TestName")
            .birthday(LocalDate.of(1994, 2, 10))
            .phone("01012345678")
            .allowToMarketingNotification(true)
            .build();
    
    mockMvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("login_id").value(signUpRequest.getLoginId()))
            .andDo(document("계정 등록",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                    fieldWithPath("login_id").type(JsonFieldType.STRING).description("계정 로그인 ID"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호"),
                    fieldWithPath("birthday").type(JsonFieldType.STRING).description("생년월일").optional(),
                    fieldWithPath("allow_to_marketing_notification").type(JsonFieldType.BOOLEAN).description("마케팅 정보 수신 동의 여부")
            ),
            responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("계정 일련번호"),
                    fieldWithPath("login_id").type(JsonFieldType.STRING).description("계정 로그인 ID")
            )));
}
```
문제는 해당 코드에는 경계가 없어서 가독성이 떨어졌다.

테스트를 위해 데이터를 초기화하는 영역은 어느 부분인지, 테스트를 실행하는 부분은 어느 부분인지, 테스트를 검증하는 부분은 어느 부분인지 도무지 이해를 할 수가 없다. 즉, 검증의 목적만 있을 뿐 테스트를 이해하기 힘들고 가독성이 떨어진다.

하지만 Controller Test에도 BDD를 적용할 수 있다.

다음의 예제를 보자.

## BDD를 적용한 예제

```java
@Test
@DisplayName("유저를 생성할 수 있다.")
void signup() throws Exception {
    // given
    SignUpRequest signUpRequest = SignUpRequest.builder()
            .loginId("test123")
            .password("password12@")
            .name("TestName")
            .birthday(LocalDate.of(1994, 2, 10))
            .phone("01012345678")
            .allowToMarketingNotification(true)
            .build();

    // when
    ResultActions result = mockMvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequest)));

    // then
    result.andExpect(status().isCreated())
            .andExpect(jsonPath("login_id").value(signUpRequest.getLoginId()));

    // docs
    result.andDo(document("계정 등록",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                    fieldWithPath("login_id").type(JsonFieldType.STRING).description("계정 로그인 ID"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호"),
                    fieldWithPath("birthday").type(JsonFieldType.STRING).description("생년월일").optional(),
                    fieldWithPath("allow_to_marketing_notification").type(JsonFieldType.BOOLEAN).description("마케팅 정보 수신 동의 여부")
            ),
            responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("계정 일련번호"),
                    fieldWithPath("login_id").type(JsonFieldType.STRING).description("계정 로그인 ID")
            )));
}
```
이제 훨씬 보기가 깔끔하다. 중요한 점은 mockMvc.perform() 메서드는 ResultActions를 반환한다는 것이다. 반환된 ResultActions로 검증과 documentation을 분리했다.

즉, 컨트롤러 테스트 부를 아래 4개의 영역으로 나누면서 가독성을 향상시킬 수 있었다.
- given
- when
- then
- (docs)

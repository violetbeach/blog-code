package com.violetbeach.testrefactor;

import com.violetbeach.testrefactor.test.DomainMailMessageModel;
import com.violetbeach.testrefactor.test.LoadMailMessagePort;
import com.violetbeach.testrefactor.test.MailMessageResponse;
import com.violetbeach.testrefactor.test.MailService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @InjectMocks
    MailService service;
    @Mock
    LoadMailMessagePort loadMailMessagePort;

    @DisplayName("loadMailMessagePort를 호출하여 MailMessageModel을 조회한다.")
    void ItReturnMailMessageModel() {
        // given
        DomainMailMessageModel expected = new DomainMailMessageModel("content");
        given(loadMailMessagePort.loadMessageModel()).willReturn(expected);

        // when
        MailMessageResponse response = service.getMail();

        // then
        assertEquals(expected.getContent(), response.content());
    }
}

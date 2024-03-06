package com.violetbeach.testrefactor.test;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailService {
    private final LoadMailMessagePort loadMailMessagePort;

    public MailMessageResponse getMail() {
        return loadMailMessagePort.loadMessageModel();
    }

}

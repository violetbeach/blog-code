package com.violetbeach.testrefactor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailService {
	private final LoadMailMessagePort loadMailMessagePort;

	public MailMessageResponse getMail() {
		return loadMailMessagePort.loadMessageModel();
	}

}

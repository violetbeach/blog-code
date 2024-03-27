package com.violetbeach.transfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.violetbeach.transfer.domain.TransferRequest;
import com.violetbeach.transfer.service.TransferService;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    TransferService transferService;

    @Test
    void test() {
        Long senderAccountId = 1L;
        Long receiverAccountId = 2L;

        TransferRequest transferRequest = new TransferRequest(
            senderAccountId,
            receiverAccountId,
            10000L
        );
        transferService.transfer(transferRequest);
    }
}

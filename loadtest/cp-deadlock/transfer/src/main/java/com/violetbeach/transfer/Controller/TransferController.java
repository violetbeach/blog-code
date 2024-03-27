package com.violetbeach.transfer.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.violetbeach.transfer.domain.TransferRequest;
import com.violetbeach.transfer.service.TransferService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/")
    void transfer() {
        Long senderAccountId = 1L;
        Long receiverAccountId = 2L;

        TransferRequest transferRequest = new TransferRequest(
            senderAccountId,
            receiverAccountId,
            10000L
        );
        transferService.transfer(transferRequest);

        System.out.println(1);
    }
}

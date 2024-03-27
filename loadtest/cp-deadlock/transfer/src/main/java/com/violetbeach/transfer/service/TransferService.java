package com.violetbeach.transfer.service;

import org.springframework.stereotype.Service;

import com.violetbeach.transfer.domain.BankingRequest;
import com.violetbeach.transfer.domain.BankingRequestRepository;
import com.violetbeach.transfer.domain.BankingRequestSavePort;
import com.violetbeach.transfer.domain.Transfer;
import com.violetbeach.transfer.domain.TransferRepository;
import com.violetbeach.transfer.domain.TransferRequest;
import com.violetbeach.transfer.service.response.BankingResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final BankingRequestSavePort bankingRequestSavePort;
    private final BankingApiAdaptor bankingApiAdaptor;

    @Transactional
    public void transfer(TransferRequest transferRequest) {
        // Transfer를 생성 후 저장
        Transfer transfer = new Transfer(
            transferRequest.senderAccountId(),
            transferRequest.receiverAccountId(),
            transferRequest.amount()
        );
        transferRepository.save(transfer);

        // BankRequest 생성 후 저장
        BankingRequest bankingRequest = BankingRequest.from(transfer);
        bankingRequestSavePort.save(bankingRequest);

        // BankingAPIAdaptor 호출
        // BankingAPI에서 처리 후 BankingRequest의 상태를 변경
        BankingResponse response = bankingApiAdaptor.banking(bankingRequest);

        // Transfer 상태 조정
        if (response.isSuccess())
            transfer.setSuccess();
        else
            transfer.setFail();
    }
}

package com.violetbeach.transfer.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.violetbeach.transfer.domain.BankingRequest;
import com.violetbeach.transfer.domain.BankingRequestRepository;
import com.violetbeach.transfer.service.response.BankingResponse;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class BankingApiAdaptor {
    private final BankingRequestRepository repository;

    @SneakyThrows
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BankingResponse banking(BankingRequest request) {
        request.setSuccess();
        repository.save(request);
        return new BankingResponse(request.getStatus() == BankingRequest.TransferStatus.SUCCESS);
    }
}

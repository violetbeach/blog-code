package com.violetbeach.transfer.domain;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BankingRequestSavePort {
    private final BankingRequestRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(BankingRequest bankingRequest) {
        repository.save(bankingRequest);
    }
}

package com.violetbeach.transfer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BankingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankingRequestId;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    public BankingRequest(TransferStatus status) {
        this.status = status;
    }

    public static BankingRequest from(Transfer transfer) {
        return new BankingRequest(transfer.getStatus());
    }

    public void setSuccess() {
        status = TransferStatus.SUCCESS;
    }

    public enum TransferStatus {
        READY, SUCCESS, FAIL
    }
}

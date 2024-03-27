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
@NoArgsConstructor
@Getter
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;
    private Long senderAccountId;
    private Long receiverAccountId;
    private Long amount;
    private BankingRequest.TransferStatus status;

    public void setSuccess() {
        this.status = BankingRequest.TransferStatus.SUCCESS;
    }

    public void setFail() {
        this.status = BankingRequest.TransferStatus.FAIL;
    }

    public Transfer(Long senderAccountId, Long receiverAccountId, Long amount) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.status = BankingRequest.TransferStatus.READY;
    }
}

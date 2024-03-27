package com.violetbeach.transfer.domain;

public record TransferRequest(
    Long senderAccountId,
    Long receiverAccountId,
    Long amount
) {
}

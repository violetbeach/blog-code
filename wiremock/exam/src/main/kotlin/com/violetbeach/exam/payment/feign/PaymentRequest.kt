package com.violetbeach.exam.payment.feign

import com.violetbeach.exam.Amount

data class PaymentRequest(
    private val userId: Long,
    private val amount: Amount
)
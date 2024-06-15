package com.violetbeach.exam.payment

import com.violetbeach.exam.Amount
import com.violetbeach.exam.payment.feign.PaymentRequest
import com.violetbeach.exam.payment.feign.PaymentResult
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentApiClient: PaymentApiClient,
) {
    fun payment(
        userId: Long,
        amount: Amount,
    ): PaymentResult {
        val request = PaymentRequest(userId, amount)
        return paymentApiClient.payment(request)
    }
}

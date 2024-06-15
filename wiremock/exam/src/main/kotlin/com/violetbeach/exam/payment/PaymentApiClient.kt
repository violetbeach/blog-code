package com.violetbeach.exam.payment

import com.violetbeach.exam.payment.feign.PaymentRequest
import com.violetbeach.exam.payment.feign.PaymentResult

open interface PaymentApiClient {
    fun payment(request: PaymentRequest): PaymentResult
}

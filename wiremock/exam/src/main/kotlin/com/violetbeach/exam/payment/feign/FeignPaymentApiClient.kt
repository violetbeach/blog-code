package com.violetbeach.exam.payment.feign

import com.violetbeach.exam.payment.PaymentApiClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "payment-api", url = "\${apis.payment.url}")
interface FeignPaymentApiClient : PaymentApiClient {
    @PostMapping(path = ["/payment"])
    override fun payment(
        @RequestBody request: PaymentRequest,
    ): PaymentResult
}

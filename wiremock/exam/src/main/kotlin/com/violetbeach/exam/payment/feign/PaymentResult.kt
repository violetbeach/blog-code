package com.violetbeach.exam.payment.feign

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentResult(
    @JsonProperty("is_success")
    val isSuccess: Boolean,
)

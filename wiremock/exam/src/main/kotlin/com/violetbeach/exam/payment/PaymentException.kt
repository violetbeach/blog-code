package com.violetbeach.exam.payment

class PaymentException(
    override val message: String,
) : RuntimeException(message)

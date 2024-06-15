package com.violetbeach.exam.order

import com.violetbeach.exam.Amount

data class Product(
    val productId: Long,
    val amount: Amount
)
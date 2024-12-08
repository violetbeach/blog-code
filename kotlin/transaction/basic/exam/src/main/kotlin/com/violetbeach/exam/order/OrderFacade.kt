package com.violetbeach.exam.order

import kotlinx.coroutines.Dispatchers
import org.springframework.transaction.annotation.Transactional

open class OrderFacade(
    val orderService: OrderService,
) {
    @Transactional
    open fun submit(
        id: Long,
        throwException: Boolean = false,
    ) {
        Dispatchers.IO(orderService.submit(id, throwException))
    }
}

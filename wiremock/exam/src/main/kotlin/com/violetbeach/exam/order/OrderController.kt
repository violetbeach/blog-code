package com.violetbeach.exam.order

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping("/order")
    fun order(
        @RequestBody orderRequest: OrderRequest,
    ): OrderResponse {
        orderService.order(orderRequest.userId, orderRequest.productId)
        return OrderResponse(isSuccess = true)
    }
}

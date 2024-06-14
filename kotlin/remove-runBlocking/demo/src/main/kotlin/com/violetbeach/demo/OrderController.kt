package com.violetbeach.demo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
){

    @GetMapping
    fun order() {
        CoroutineScope(Dispatchers.Order).launch {
            orderService.order()
        }
    }
}
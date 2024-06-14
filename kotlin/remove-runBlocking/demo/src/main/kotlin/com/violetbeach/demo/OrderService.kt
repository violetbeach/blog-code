package com.violetbeach.demo

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class OrderService {

    suspend fun order() {
        runBlocking(Dispatchers.Order) {
            delay(100)
            println("주문이 완료되었습니다.")
        }
    }
}
package com.violetbeach.curcuitexam

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController {

    @GetMapping("/order")
    @CircuitBreaker(name = "order", fallbackMethod = "orderFallback")
    fun order(): String {
        println("Order 요청")
        Thread.sleep(3000)
        return "주문이 완료되었습니다."
    }

    fun orderFallback(e: Throwable): String {
        println("Fallback 호출")
        return "잠시 후 다시 시도해주세요. cause: ${e.message}"
    }
}
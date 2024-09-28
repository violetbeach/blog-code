package com.violetbeach.exam.order

import kotlinx.coroutines.delay
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    var count = 0

    @Transactional
    suspend fun submit(
        id: Long,
        throwException: Boolean = false,
    ) {
        println(Thread.currentThread().name)
        println(count++)
        delay(100)
        val order = orderRepository.findById(id).get()
        order.submit()
        orderRepository.save(order)
        delay(100)
        if (throwException) {
            throw IllegalStateException("테스트 위한 에러")
        }
    }

    @Transactional
    fun submitNotSuspend(
        id: Long,
        throwException: Boolean = false,
    ) {
        println(Thread.currentThread().name)
        println(count++)
        val order = orderRepository.findById(id).get()
        order.submit()
        orderRepository.save(order)
        if (throwException) {
            throw IllegalStateException("테스트 위한 에러")
        }
    }
}

package com.violetbeach.exam.order

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setup() {
        runBlocking {
            orderRepository.deleteAll()
        }
    }

    @Test
    fun `정상 저장 테스트`() {
        // given
        val order =
            runBlocking {
                orderRepository.save(
                    Order(
                        status = OrderStatus.READY,
                    ),
                )
            }

        // when
        runBlocking(Dispatchers.IO) {
            orderService.submit(order.id!!)
        }

        // then
        val result =
            runBlocking {
                orderRepository.findById(order.id!!)
            }
        assertThat(result.status).isEqualTo(OrderStatus.SUBMITTED)
    }

    @Test
    fun `롤백 동작 테스트`() {
        // given
        val order =
            runBlocking {
                orderRepository.save(
                    Order(
                        status = OrderStatus.READY,
                    ),
                )
            }

        // when
        runCatching {
            runBlocking {
                orderService.submit(order.id!!, true)
            }
        }

        // then
        val result = runBlocking { orderRepository.findById(order.id!!) }
        assertThat(result.status).isEqualTo(OrderStatus.READY)
    }

    @Test
    fun `동시성 테스트`() =
        runTest {
            // given
            repeat(2000) {
                orderRepository.save(Order(status = OrderStatus.READY))
            }

            // when
            val jobs = ArrayList<Job>()
            for (i in 1L..2000L) {
                val job =
                    launch(Dispatchers.IO) {
                        orderService.submit(
                            id = i,
                            throwException = i % 10 == 0L,
                        )
                    }
                jobs.add(job)
            }
            jobs.joinAll()

            // then
            val submittedOrders = orderRepository.findAll().toList().filter { it.status == OrderStatus.SUBMITTED }
            assertThat(submittedOrders).hasSize(1800)
        }
}

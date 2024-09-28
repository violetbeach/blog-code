package com.violetbeach.exam.order

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
        orderRepository.deleteAll()
    }

    @Test
    fun `정상 저장 테스트`() {
        // given
        val order =
            orderRepository.save(
                Order(
                    id = 1L,
                    status = OrderStatus.READY,
                ),
            )

        // when
        runBlocking(Dispatchers.IO) {
            orderService.submit(order.id)
        }

        // then
        val result = orderRepository.findById(order.id).get()
        assertThat(result.status).isEqualTo(OrderStatus.SUBMITTED)
    }

    @Test
    fun `롤백 동작 테스트`() {
        // given
        val order =
            orderRepository.save(
                Order(
                    id = 1L,
                    status = OrderStatus.READY,
                ),
            )

        // when
        runCatching {
            runBlocking {
                orderService.submit(order.id, true)
            }
        }

        // then
        val result = orderRepository.findById(order.id).get()
        assertThat(result.status).isEqualTo(OrderStatus.READY)
    }

//    @Test
//    @Disabled
//    fun `동시성 테스트`() {
//        // given
//        for (i in 1L..500L) {
//            orderRepository.save(
//                Order(
//                    id = i,
//                    status = OrderStatus.READY,
//                ),
//            )
//        }
//
//        // when
//        val jobs = ArrayList<Job>()
//        runBlocking {
//            for (i in 1L..500L) {
//                val job =
//                    launch(Dispatchers.IO) {
//                        runCatching {
//                            orderService.submit(
//                                id = i,
//                                throwException = i % 2 == 0L,
//                            )
//                        }
//                    }
//                jobs.add(job)
//            }
//            jobs.joinAll()
//        }
//
//        // then
//        val submittedOrders = orderRepository.findAll().filter { it.status == OrderStatus.SUBMITTED }
//        assertThat(submittedOrders).hasSize(250)
//    }
}

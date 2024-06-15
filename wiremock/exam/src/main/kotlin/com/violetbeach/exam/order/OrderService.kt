package com.violetbeach.exam.order

import com.violetbeach.exam.payment.PaymentException
import com.violetbeach.exam.payment.PaymentService
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val productService: ProductService,
    private val paymentService: PaymentService,
) {
    fun order(
        userId: Long,
        productId: Long,
    ) {
        val product = productService.get(productId)
        productService.verify(product)
        val paymentResult = paymentService.payment(userId, product.amount)
        if (!paymentResult.isSuccess) {
            throw PaymentException("주문이 실패했습니다.")
        }
        // 주문 로직
        println("주문 완료!")
    }
}

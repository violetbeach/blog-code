package com.violetbeach.exam.order

import com.violetbeach.exam.Amount
import org.springframework.stereotype.Component

@Component
class ProductService {
    fun verify(product: Product) {
    }

    fun get(productId: Long): Product = Product(productId, Amount(10000))
}

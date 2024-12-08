package com.violetbeach.exam.order

import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "order_table")
@NoArgsConstructor
class Order(
    @Id
    var id: Long? = null,
    var status: OrderStatus,
) {
    fun submit() {
        status = OrderStatus.SUBMITTED
    }
}

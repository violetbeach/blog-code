package com.violetbeach.exam.order

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.NoArgsConstructor

@Entity
@Table(name = "orders")
@NoArgsConstructor
class Order(
    @Id
    var id: Long,
    var status: OrderStatus,
) {
    fun submit() {
        status = OrderStatus.SUBMITTED
    }
}

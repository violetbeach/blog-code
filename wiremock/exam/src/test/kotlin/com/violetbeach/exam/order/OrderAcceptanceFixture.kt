package com.violetbeach.exam.order

import com.violetbeach.exam.support.testUserId

class OrderAcceptanceFixture {
    companion object {
        fun 뿌링클_1마리_주문_요청_V2() {
            val map = HashMap<String, Any>()
            map.put("user_id", testUserId)
            map.put("product_id", 1)
        }
    }
}

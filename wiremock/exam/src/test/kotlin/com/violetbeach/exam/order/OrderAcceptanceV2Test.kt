package com.violetbeach.exam.order

import com.violetbeach.exam.support.AcceptanceContext.Companion.invokePost
import com.violetbeach.exam.support.BaseAcceptanceV2Test
import com.violetbeach.exam.support.testUserId
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class OrderAcceptanceV2Test : BaseAcceptanceV2Test() {
    @Test
    fun order() {
        val response = 뿌링클을_주문한다()

        val 응답_데이터 = response.`as`(Map::class.java)
        assertThat(응답_데이터.get("is_success")).isEqualTo(true)
    }

    fun 뿌링클을_주문한다(): ExtractableResponse<Response> = invokePost("/order", 뿌링클_1마리_주문_요청())

    fun 뿌링클_1마리_주문_요청() {
        val map = HashMap<String, Any>()
        map.put("user_id", testUserId)
        map.put("product_id", 1)
    }
}

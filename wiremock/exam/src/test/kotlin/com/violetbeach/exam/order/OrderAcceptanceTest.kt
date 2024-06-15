package com.violetbeach.exam.order

import com.violetbeach.exam.support.AcceptanceContext.Companion.invokePost
import com.violetbeach.exam.support.BaseAcceptanceTest
import com.violetbeach.exam.support.testUserId
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class OrderAcceptanceTest : BaseAcceptanceTest() {
    @Test
    @DisplayName("사용자는 상품을 주문할 수 있다.")
    fun order() {
        val response = 뿌링클을_주문한다()
        val 응답_데이터 = 주문_응답(response)
        assertThat(응답_데이터.isSuccess).isEqualTo(true)
    }

    fun 뿌링클을_주문한다(): ExtractableResponse<Response> = invokePost("/order", 뿌링클_1마리_주문_요청())

    fun 주문_응답(response: ExtractableResponse<Response>) = response.`as`(OrderResponse::class.java)
}

fun 뿌링클_1마리_주문_요청(): OrderRequest = OrderRequest(testUserId, 1L)

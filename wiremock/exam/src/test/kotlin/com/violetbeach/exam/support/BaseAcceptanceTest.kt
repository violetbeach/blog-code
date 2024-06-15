package com.violetbeach.exam.support

import com.violetbeach.exam.payment.PaymentApiClient
import com.violetbeach.exam.payment.feign.PaymentRequest
import com.violetbeach.exam.payment.feign.PaymentResult
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@ExtendWith(MockitoExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseAcceptanceTest {
    @LocalServerPort
    val port: Int? = null

    @MockBean
    lateinit var paymentApiClient: PaymentApiClient

    @BeforeEach
    fun setup() {
        RestAssured.port = port!!

        given(paymentApiClient.payment(any(PaymentRequest::class.java)))
            .willReturn(PaymentResult(true))
    }

    private inline fun <reified T> any(type: Class<T>): T = Mockito.any(type)
}

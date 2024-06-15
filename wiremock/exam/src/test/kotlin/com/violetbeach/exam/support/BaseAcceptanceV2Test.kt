package com.violetbeach.exam.support

import com.violetbeach.exam.wiremock.WireMockContext
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseAcceptanceV2Test {
    @LocalServerPort
    val port: Int? = null

    @BeforeEach
    fun setup() {
        RestAssured.port = port!!

        WireMockContext.setupPaymentApi()
    }
}

package com.violetbeach.exam.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class WireMockContext {
    companion object {
        fun setupPaymentApi() {
            stubFor(
                WireMock
                    .post(WireMock.urlMatching("/payment"))
                    .willReturn(
                        WireMock
                            .aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBodyFile("payload/payment-response.json"),
                    ),
            )
        }
    }
}

package com.violetbeach.exam.support

import io.restassured.RestAssured
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.springframework.http.MediaType

class AcceptanceContext {
    companion object {
        fun invokePost(
            path: String,
            data: Any,
        ): ExtractableResponse<Response> =
            RestAssured
                .given()
                .log()
                .all()
                .body(data)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .`when`()
                .post(path)
                .then()
                .log()
                .all()
                .extract()
    }
}

const val testUserId = 1L

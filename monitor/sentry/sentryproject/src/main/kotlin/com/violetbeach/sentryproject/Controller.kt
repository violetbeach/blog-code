package com.violetbeach.sentryproject

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @RequestMapping("/order")
    fun order() {
        try {
            throw BaeminException(ErrorCode.USER_NOT_FOUND)
        } catch (e: Exception) {
            log.error("유저 찾기 실패. ${e.message}", e)
        }
    }
}

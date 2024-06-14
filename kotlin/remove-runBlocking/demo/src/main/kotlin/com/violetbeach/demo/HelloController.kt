package com.violetbeach.demo

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.coroutines.coroutineContext

@RestController
@RequestMapping("/hello")
class HelloController {
    private val log = LoggerFactory.getLogger(HelloController::class.java)

    @GetMapping
    suspend fun hello() {
        log.info("context: {}", coroutineContext)
        log.info("thread: {}", Thread.currentThread().name)
    }

}
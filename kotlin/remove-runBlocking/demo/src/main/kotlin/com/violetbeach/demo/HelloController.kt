package com.violetbeach.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController {
    private val log = logger<GreetController>()

    @GetMapping
    suspend fun hello() {
        log.info("context: {}", coroutineContext)
        log.info("thread: {}", Thread.currentThread().name)
    }
}
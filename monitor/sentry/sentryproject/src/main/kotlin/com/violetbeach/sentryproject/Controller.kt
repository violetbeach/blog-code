package com.violetbeach.sentryproject

import io.sentry.Sentry
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    @RequestMapping("/test")
    fun test() {
        try {
            throw RuntimeException("catch!!")
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }
}

package com.violetbeach.sentryproject

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    
    @RequestMapping("/health")
    fun health(): String {
        return "OK"
    }
}
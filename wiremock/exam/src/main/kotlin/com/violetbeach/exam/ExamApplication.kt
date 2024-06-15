package com.violetbeach.exam

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class ExamApplication

fun main(args: Array<String>) {
    runApplication<ExamApplication>(*args)
}

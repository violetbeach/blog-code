package com.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MainTest {

    val objectMapper = ObjectMapper()
        .registerModules(
            KotlinModule.Builder()
                .build()
        )

    @Test
    fun testClassA() {
        val test = ClassA("A")
        objectMapper.writeValueAsString(test)

        Assertions.assertEquals("*", test.field)
    }

    @Test
    fun testClassB() {
        val test = Super.Inner.ClassB("B")
        objectMapper.writeValueAsString(test)

        Assertions.assertEquals("*", test.field)
    }
}

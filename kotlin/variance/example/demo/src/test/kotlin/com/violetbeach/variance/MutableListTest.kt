package com.violetbeach.exam

import kotlin.test.Test

class MutableListTest {
    @Test
    fun main() {
        val numbers: MutableList<Int> = mutableListOf(1, 2, 3) // MutableList<Int>
        printList(numbers) // 컴파일 에러 O
    }

    private fun printList(list: MutableList<Any>) {
        println(list.joinToString())
    }
}

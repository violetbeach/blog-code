package com.violetbeach.exam

import java.util.Comparator
import kotlin.test.Test

class VarianceTest {
    @Test
    fun main() {
        val numbers: List<Int> = listOf(1, 2, 3)
        printList(numbers) // 컴파일 에러 X
    }

    private fun printList(list: List<Number>) {
        println(list.joinToString())
    }


    val test: Comparable
}

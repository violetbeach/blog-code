package com.violetbeach.demo

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Dispatchers {
    companion object {
        @JvmStatic
        val Order: CoroutineDispatcher = Dispatchers.Default
    }
}
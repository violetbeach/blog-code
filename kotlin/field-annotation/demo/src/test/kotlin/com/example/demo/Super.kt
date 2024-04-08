package com.example.demo

class Super {
    class Inner {
        data class ClassB(
            @Masking
            val field: String? = null,
        )

        data class ClassC(
            val field: String = ""
        )
    }
}
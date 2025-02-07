package com.violetbeach.variance.invariance


interface Cooker<T> {
    fun cook(item: T)
}

class FoodCooker : Cooker<Food> {
    override fun cook(item: Food) {
        println("Cooking ${item.name}")
    }
}

fun main() {
    val cooker: Cooker<Food> = FoodCooker()
    cooker.cook(Ramen()) // 컴파일 에러!!
}

class Food

class Ramen:Food()

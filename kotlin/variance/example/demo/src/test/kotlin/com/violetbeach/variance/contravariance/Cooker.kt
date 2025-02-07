package com.violetbeach.variance.contravariance

interface Cooker<out T> {
    fun cook(item: T)
}

class FoodCooker : Cooker<Food> {
    override fun cook(item: Food) {
        println("Cooking ${item.name}")
    }
}

fun main() {
    val cooker: Cooker<Ramen> = FoodCooker()
    cooker.cook(Ramen()) // 정상 동작!
}

open class SomeThing

open class Food: SomeThing()

class Ramen: Food()

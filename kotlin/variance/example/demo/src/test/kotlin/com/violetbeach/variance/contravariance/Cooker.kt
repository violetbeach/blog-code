package com.violetbeach.variance.contravariance

interface Cooker<in T> {
    fun cook(item: T)
}

class RamenCooker : Cooker<Ramen> {
    override fun cook(item: Ramen) {
        println("Cooking Ramen")
    }
}

class Kichen(
    val cooker: Cooker<Food>
)

fun main() {
    Kichen(cooker = RamenCooker())
}

open class SomeThing

open class Food: SomeThing()

class Ramen: Food()

package com.violetbeach.variance.contravariance

interface CustomComparable<in T> {
    operator fun compareTo(other: T): Int
}

class AnimalComparable: Comparable<Animal> {
    override fun compareTo(other: Animal): Int {
        return 0
    }
}

// ✅ 반공변이므로, Dog에도 사용 가능
val dogComparable: Comparable<Dog> = AnimalComparable()

open class Animal

class Dog : Animal()

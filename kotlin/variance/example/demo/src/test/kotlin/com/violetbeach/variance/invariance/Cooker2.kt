package com.violetbeach.variance.invariance

import com.violetbeach.variance.contravariance.CustomComparable


interface EventHandler<in T> {
    fun handle(event: T)
}

class ClickEventHandler : EventHandler<ClickEvent> {
    override fun handle(event: ClickEvent) {
        println("Click event handled: $event")
    }
}

fun main() {
    val generalHandler: EventHandler<ClickEvent> = ClickEventHandler()  // 컴파일 에러
}

open class Event

class ClickEvent : Event()

class User(var id: Int, var name: String) : CustomComparable<Number> {
    // id로 비교
    override fun compareTo(other: Int): Int =
        when {
            this.id > other.id -> 1
            this.id < other.id -> -1
            else -> 0
        }
}

package com.violetbeach.sentryproject

class BaeminException(
    val code: ErrorCode,
) : RuntimeException(code.message)

enum class ErrorCode(
    val message: String,
) {
    USER_NOT_FOUND("유저가 존재하지 않습니다."),
    ORDER_NOT_FOUND("주문이 존재하지 않습니다."),
}

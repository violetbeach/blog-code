package com.violetbeach.rpground.order

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    val stringRedisTemplate: StringRedisTemplate,
) {
    @GetMapping
    fun order(): String {
        stringRedisTemplate.opsForList().leftPush("Users", "1")
        stringRedisTemplate.opsForList().leftPush("Users", "2")

        stringRedisTemplate.opsForSet().add("Animal", "Tiger")
        stringRedisTemplate.opsForSet().add("Animal", "Cat")

        stringRedisTemplate.opsForValue().set("App-Password", "VioletBeach")

        stringRedisTemplate.opsForHash<String, String>().put("UserNames", "1", "Jerry")
        stringRedisTemplate.opsForHash<String, String>().put("UserNames", "2", "Adward")
        stringRedisTemplate.opsForHash<String, String>().put("UserNames", "3", "Harry")

        return "주문 완료"
    }
}

package com.violetbeach.exam.order

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderRequest(
    var userId: Long,
    @JsonProperty("productId")
    var productId: Long,
)

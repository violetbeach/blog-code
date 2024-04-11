package com.example.blog

import com.fasterxml.jackson.annotation.JsonInclude

open class ClassA(
    @Masking
    @JsonInclude
    var fieldStudy: String? = null
) {

}
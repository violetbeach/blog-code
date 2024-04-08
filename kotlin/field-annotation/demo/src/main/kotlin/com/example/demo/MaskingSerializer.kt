package com.example.demo

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer

class MaskingSerializer(
    private val withMasking: Boolean = false
) : JsonSerializer<Any>(), ContextualSerializer {

    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider?) {
        if (withMasking) gen.writeString("*")
    }

    override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*> {
        val a = property?.getAnnotation(Masking::class.java) != null
        return MaskingSerializer(
            property?.getAnnotation(Masking::class.java) != null
        )
    }

}
package com.weekly.weeklychecklist.database

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class MutableStateTypeAdapter<T>: JsonSerializer<List<T>>, JsonDeserializer<ArrayList<T>> {
    override fun serialize(
        src: List<T>,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return context.serialize(src)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ArrayList<T> {
        return arrayListOf(context.deserialize<T>(json, typeOfT))
    }
}
package com.weekly.weeklychecklist.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @TypeConverter
    fun localDateToString(value: LocalDateTime): String = value.toString()

    @TypeConverter
    fun stringToLocalDate(value: String): LocalDateTime = LocalDateTime.parse(value)
}
package com.weekly.weeklychecklist.database

import androidx.compose.runtime.MutableState
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.weekly.weeklychecklist.vm.CheckListInfo
import java.time.LocalDate

class CheckListConverters {
    @TypeConverter
    fun listToJson(value: List<CheckListInfo>): String?{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<CheckListInfo> {
        val listType = object: TypeToken<List<CheckListInfo>>(){}.type
        val gson = Gson()
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun dateToJson(value: LocalDate): String?{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToDate(value: String): LocalDate{
        val dateType = object: TypeToken<LocalDate>() {}.type
        return Gson().fromJson(value, dateType)
    }
}
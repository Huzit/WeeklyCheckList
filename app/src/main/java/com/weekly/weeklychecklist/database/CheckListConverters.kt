package com.weekly.weeklychecklist.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weekly.weeklychecklist.vm.CheckListInfo

class CheckListConverters {
    @TypeConverter
    fun listToJson(value: List<CheckListInfo>): String?{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<CheckListInfo> {
        val listType = object: TypeToken<List<CheckListInfo>>(){}.type
        return Gson().fromJson(value, listType)

    }
}
package com.weekly.weeklychecklist.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.weekly.weeklychecklist.vm.CheckListInfo

class CheckListConverters {
    @TypeConverter
    fun listToJson(value: CheckListEntity): String?{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): CheckListEntity? {
        return Gson().fromJson(value, CheckListEntity::class.java)
    }
}
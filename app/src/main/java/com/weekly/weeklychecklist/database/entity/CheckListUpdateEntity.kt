package com.weekly.weeklychecklist.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.weekly.weeklychecklist.converters.LocalDateTimeConverter
import java.time.LocalDateTime

@Entity("CheckListUpdate")
@TypeConverters(LocalDateTimeConverter::class)
data class CheckListUpdateEntity(
    @ColumnInfo("list_name")
    val listName: String,
    @ColumnInfo("is_update")
    var isUpdate: Boolean,
    @ColumnInfo("register_time")
    var registerTime: LocalDateTime
){
    @PrimaryKey(autoGenerate = true)
    var idx: Long = 0
}
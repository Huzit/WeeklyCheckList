package com.weekly.weeklychecklist.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.converters.LocalDateTimeConverter
import com.weekly.weeklychecklist.database.CheckListConverter
import java.time.LocalDateTime

@Entity(tableName = "CheckList")
@TypeConverters(CheckListConverter::class, LocalDateTimeConverter::class)
data class CheckListEntity(
    @ColumnInfo(name = "list_name")
    var listName: String,
    @ColumnInfo (name = "checklistContent")
    var checklistContent: String,
    @ColumnInfo (name = "restartWeek")
    var restartWeek: MutableSet<MyDayOfWeek>,
    @ColumnInfo (name = "done")
    var done: Boolean = false,
    @ColumnInfo("register_time")
    var registerTime: LocalDateTime

){
    @PrimaryKey(autoGenerate = true)
    var idx: Long = 0
}

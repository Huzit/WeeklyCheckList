package com.weekly.weeklychecklist.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.converters.LocalDateConverter
import com.weekly.weeklychecklist.database.CheckListConverter
import java.time.LocalDate

@Entity(tableName = "CheckList")
@TypeConverters(CheckListConverter::class, LocalDateConverter::class)
data class CheckListEntity(
    @ColumnInfo(name = "list_name")
    var listName: String,
    @ColumnInfo (name = "checklistContent")
    var checklistContent: String,
    @ColumnInfo (name = "restartWeek")
    var restartWeek: Set<MyDayOfWeek>,
    @ColumnInfo (name = "done")
    var done: Boolean = false,
//    @ColumnInfo(name = "is_updated")
//    var isUpdated: Boolean,
    @ColumnInfo(name = "last_updated_date")
    var lastUpdatedDate: LocalDate
){
    @PrimaryKey(autoGenerate = true)
    var idx: Long = 0
}

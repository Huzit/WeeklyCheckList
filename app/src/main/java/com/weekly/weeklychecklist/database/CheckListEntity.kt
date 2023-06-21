package com.weekly.weeklychecklist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.weekly.weeklychecklist.vm.CheckListInfo
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "checklist")
@TypeConverters(CheckListConverters::class)
data class CheckListEntity(
    @ColumnInfo(name = "list_name") var listName: String,
    @ColumnInfo(name = "checklist_info") var checkLists: List<CheckListInfo>,
    @ColumnInfo(name = "is_updated") var isUpdated: Boolean,
    @ColumnInfo(name = "last_updated_date") var lastUpdatedDate: LocalDate
){
    @PrimaryKey(autoGenerate = true)
    var idx: Long = 0
}

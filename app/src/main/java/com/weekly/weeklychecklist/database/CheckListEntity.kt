package com.weekly.weeklychecklist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.weekly.weeklychecklist.vm.CheckListInfo

@Entity(tableName = "checklist")
@TypeConverters(CheckListConverters::class)
data class CheckListEntity(
    @ColumnInfo(name = "checklist_info") var checkLists: List<CheckListInfo>
){
    @PrimaryKey(autoGenerate = true)
    var idx: Long = 0
}

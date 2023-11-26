package com.weekly.weeklychecklist.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDateTime


@Entity("CheckListUpdate")
data class CheckListUpdateEntity(
    @ColumnInfo("list_name")
    val listName: String,
    @ColumnInfo("is_update")
    val isUpdate: Boolean,
    @ColumnInfo("register_time")
    val registerTime: LocalDateTime
)
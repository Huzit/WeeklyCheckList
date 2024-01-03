package com.weekly.weeklychecklist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.weekly.weeklychecklist.converters.CheckListConverter
import com.weekly.weeklychecklist.converters.LocalDateTimeConverter
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity
import java.time.LocalDateTime

@Dao
@TypeConverters(CheckListConverter::class, LocalDateTimeConverter::class)
interface CheckListUpdateDao{
    @Query("select * from CheckListUpdate where list_name = :listName")
    fun getCheckListUpdateDate(vararg listName: String): List<CheckListUpdateEntity>
    
    @Insert
    fun insertCheckListUpdateDate(vararg checkListUpdateEntity: CheckListUpdateEntity)
    
    @Query("""
    update CheckListUpdate
        set list_name = :listName,
        is_update = :isUpdate,
        register_time = :registerTime
        where idx = :idx
    """)
    fun updateCheckListUpdateDate(
        listName: String,
        isUpdate: Boolean,
        registerTime: LocalDateTime,
        idx: Long
    )
}
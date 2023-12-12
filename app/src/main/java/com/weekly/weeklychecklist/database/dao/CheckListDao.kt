package com.weekly.weeklychecklist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.converters.CheckListConverter
import com.weekly.weeklychecklist.converters.LocalDateTimeConverter
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import java.time.LocalDateTime

@Dao
@TypeConverters(CheckListConverter::class, LocalDateTimeConverter::class)
interface CheckListDao {
    @Insert
    fun insertCheckList(vararg checkListEntity: CheckListEntity)

    @Query("SELECT * FROM CheckList WHERE listName = :listName")
    fun getCheckList(vararg listName: String): List<CheckListEntity>

    @Query("DELETE FROM CheckList WHERE listName = :listName")
    fun deleteCheckList(vararg listName: String)
    @Query("""
            Update CheckList
                Set listName = :listName,
                    checklistContent = :checkListContent,
                    restartWeek = :restartWeek,
                    done = :done,
                    register_time = :lastUpdatedDate
            where idx = :idx
            """)
    fun updateCheckList(
        idx: Long,
        listName: String,
        checkListContent: String,
        restartWeek: MutableSet<MyDayOfWeek>,
        done: Boolean,
        lastUpdatedDate: LocalDateTime
    )
}
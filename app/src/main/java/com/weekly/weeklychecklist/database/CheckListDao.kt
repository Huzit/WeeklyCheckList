package com.weekly.weeklychecklist.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.weekly.weeklychecklist.vm.CheckListInfo

@Dao
interface CheckListDao {
    @Insert
    suspend fun insertCheckList(vararg checkListEntity: CheckListEntity)

    @Query("SELECT * FROM checklist WHERE list_name = :listName ")
    suspend fun getCheckList(vararg listName: String): List<CheckListEntity>

    @Query("DELETE FROM checklist WHERE list_name = :listName")
    suspend fun deleteCheckList(vararg listName: String)

    @Update
    suspend fun updateCheckList(vararg checkListEntity: CheckListEntity)
}
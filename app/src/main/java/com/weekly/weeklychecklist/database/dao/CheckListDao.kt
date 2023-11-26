package com.weekly.weeklychecklist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.weekly.weeklychecklist.database.entity.CheckListEntity

@Dao
interface CheckListDao {
    @Insert
    suspend fun insertCheckList(vararg checkListEntity: CheckListEntity)

    @Query("SELECT * FROM CheckList WHERE list_name = :listName ")
    fun getCheckList(vararg listName: String): ArrayList<CheckListEntity>

    @Query("DELETE FROM CheckList WHERE list_name = :listName")
    suspend fun deleteCheckList(vararg listName: String)

    @Update
    suspend fun updateCheckList(vararg checkListEntity: CheckListEntity)
}
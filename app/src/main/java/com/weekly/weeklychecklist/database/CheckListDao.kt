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
    suspend fun insertCheckList(vararg checkListInfo: CheckListEntity)

    @Query("SELECT * FROM checklist")
    suspend fun getCheckList(): CheckListEntity

    @Query("DELETE FROM checklist")
    suspend fun deleteCheckList()

    @Update
    suspend fun updateCheckList(vararg checkList: CheckListEntity)

}
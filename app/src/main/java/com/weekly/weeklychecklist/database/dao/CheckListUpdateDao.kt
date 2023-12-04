package com.weekly.weeklychecklist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity

@Dao
interface CheckListUpdateDao{
    @Query("select * from CheckListUpdate where list_name = :listName")
    fun getCheckListUpdated(vararg listName: String): List<CheckListUpdateEntity>
    
    @Insert
    fun insertCheckListUpdate(vararg checkListUpdateEntity: CheckListUpdateEntity)
    
    @Update
    fun updateCheckListUpdate(vararg checkListUpdateEntity: CheckListUpdateEntity)
}
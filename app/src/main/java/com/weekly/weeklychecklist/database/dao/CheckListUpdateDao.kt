package com.weekly.weeklychecklist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity

@Dao
interface CheckListUpdateDao {
    @Query("select * from CheckListUpdate where list_name = :listName")
    fun getCheckListUpdated(vararg listName: String): ArrayList<CheckListUpdateEntity>
    
    @Insert
    fun insertCheckListUpdate(vararg checkListUpdateEntity: CheckListUpdateEntity)
}
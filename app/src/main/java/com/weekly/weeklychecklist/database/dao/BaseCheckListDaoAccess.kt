package com.weekly.weeklychecklist.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity

@Dao
interface BaseCheckListDaoAccess<T> {
    @Query("")
    fun getCheckList(vararg listName: String): ArrayList<T>
}

@Dao
interface BaseCheckListUpdateDaoAccess<T> {
    @Query("")
    fun getCheckListUpdated(vararg listName: String): ArrayList<T>
}

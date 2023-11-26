package com.weekly.weeklychecklist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weekly.weeklychecklist.database.dao.CheckListDao
import com.weekly.weeklychecklist.database.dao.CheckListUpdateDao
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity

@Database(entities = [CheckListEntity::class, CheckListUpdateEntity::class], version = 1)
abstract class CheckListDatabase: RoomDatabase() {
    abstract fun checkListDao(): CheckListDao
    abstract fun checkListUpdateDao(): CheckListUpdateDao
}
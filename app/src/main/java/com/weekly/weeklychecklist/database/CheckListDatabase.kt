package com.weekly.weeklychecklist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CheckListEntity::class], version = 1)
abstract class CheckListDatabase: RoomDatabase() {
    abstract fun checklistDao(): CheckListDao
}
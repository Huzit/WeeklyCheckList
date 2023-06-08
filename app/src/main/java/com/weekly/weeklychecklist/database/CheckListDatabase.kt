package com.weekly.weeklychecklist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter

@Database(entities = [CheckListEntity::class], version = 1)
//TODO gsdfs
//@TypeConverter(CheckListConverters::class)
abstract class CheckListDatabase: RoomDatabase() {
    abstract fun checklistDao(): CheckListDao
}
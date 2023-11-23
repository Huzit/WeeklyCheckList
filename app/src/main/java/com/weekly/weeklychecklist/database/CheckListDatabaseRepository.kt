package com.weekly.weeklychecklist.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.weekly.weeklychecklist.MyDayOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

class CheckListDatabaseRepository() {
    private lateinit var db: CheckListDatabase
    private lateinit var dao: CheckListDao

    companion object: SingletonHolderNoProperty<CheckListDatabaseRepository>(::CheckListDatabaseRepository)

    //초기화
    fun initDatabase(context: Context) {
        db = Room.databaseBuilder(
            context,
            CheckListDatabase::class.java,
            "checklist"
        ).build()
        dao = db.checklistDao()
    }

    suspend fun insertCheckList(
        listName: String,
        checkListContent: String,
        restartWeek: Set<MyDayOfWeek>,
        done: Boolean,
        isUpdated: Boolean,
        lastUpdatedDate: LocalDate
    ) {
        try {
            dao.insertCheckList(
                CheckListEntity(
                    listName,
                    checkListContent,
                    restartWeek,
                    done,
                    isUpdated,
                    lastUpdatedDate
                )
            )
        } catch (e: RuntimeException) {
            Log.e("WeeklyCheckList " + javaClass.simpleName, "이미 존재 하는 테이블 입니다.")
        }
    }

    //select
    suspend fun getCheckList(listName: String): List<CheckListEntity> {
        return dao.getCheckList(listName)
    }

    //update
    suspend fun updateCheckList(
        listName: String,
        checkListContent: String,
        restartWeek: Set<MyDayOfWeek>,
        done: Boolean,
        isUpdated: Boolean,
        lastUpdatedDate: LocalDate
    ) {
        try {
            val checkList = dao.getCheckList(listName)
            if (checkList.isEmpty()) {
                insertCheckList(listName, checkListContent, restartWeek, done, isUpdated, lastUpdatedDate)
            }
            else {

            }
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Database sync(Update & Insert) is Failed")
        }
    }

    suspend fun updateAll(){

    }

    //delete
    fun deleteDatabase() = CoroutineScope(Dispatchers.IO).launch {
        try {
            dao.deleteCheckList()
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Database Delete is Failed")
        }
    }
}
package com.weekly.weeklychecklist.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.database.dao.CheckListDao
import com.weekly.weeklychecklist.database.dao.CheckListUpdateDao
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity
import com.weekly.weeklychecklist.util.SingletonHolderNoProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

class CheckListDatabaseRepository() {
    private val TAG = javaClass.simpleName
    private lateinit var db: CheckListDatabase
    private lateinit var checkListDao: CheckListDao
    private lateinit var checkListUpdateDao: CheckListUpdateDao

    companion object: SingletonHolderNoProperty<CheckListDatabaseRepository>(::CheckListDatabaseRepository)

    //초기화
    fun initDatabase(context: Context) {
        db = Room.databaseBuilder(
            context,
            CheckListDatabase::class.java,
            "CheckListDatabase"
        ).build()
        checkListDao = db.checkListDao()
        checkListUpdateDao = db.checkListUpdateDao()
    }
    
    suspend fun insertCheckListUpdate(
        listName: String,
        isUpdated: Boolean,
        registerTime: LocalDateTime
    ){
        try{
            checkListUpdateDao.insertCheckListUpdate(
                CheckListUpdateEntity(
                    listName,
                    isUpdated,
                    registerTime
                )
            )
        }catch (e: RuntimeException){
            Log.e(TAG, "이미 존재 하는 테이블 입니다. ${e.stackTraceToString()}")
        }
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
            checkListDao.insertCheckList(
                CheckListEntity(
                    listName,
                    checkListContent,
                    restartWeek,
                    done,
//                    isUpdated,
                    lastUpdatedDate
                )
            )
        } catch (e: RuntimeException) {
            Log.e(TAG, "이미 존재 하는 테이블 입니다. ${e.stackTraceToString()}")
        }
    }

    //select
    fun getCheckList(listName: String): ArrayList<CheckListEntity> {
        return checkListDao.getCheckList(listName)
    }
    
    fun getCheckListUpdate(listName: String): ArrayList<CheckListUpdateEntity> {
        return checkListUpdateDao.getCheckListUpdated(listName)
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
            val checkList = checkListDao.getCheckList(listName)
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
            checkListDao.deleteCheckList()
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Database Delete is Failed")
        }
    }
}
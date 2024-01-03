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
import java.io.IOException
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
    
    fun insertCheckListUpdate(
        listName: String,
        isUpdated: Boolean,
        registerTime: LocalDateTime
    ){
        try{
            checkListUpdateDao.insertCheckListUpdateDate(
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

    suspend fun updateCheckListUpdate(
        checkListUpdateEntity: CheckListUpdateEntity,
    ){
        //TODO UpdateQuery 필요
        checkListUpdateDao.updateCheckListUpdateDate(
            checkListUpdateEntity.listName,
            checkListUpdateEntity.isUpdate,
            checkListUpdateEntity.registerTime,
            checkListUpdateEntity.idx
        )
    }

    fun insertCheckList(
        listName: String,
        checkListContent: String,
        restartWeek: MutableSet<MyDayOfWeek>,
        done: Boolean,
        lastUpdatedDate: LocalDateTime
    ) {
        try {
            Log.d(TAG, "insert is successful")
            checkListDao.insertCheckList(
                CheckListEntity(
                    listName,
                    checkListContent,
                    restartWeek,
                    done,
                    lastUpdatedDate
                )
            )
        } catch (e: RuntimeException) {
            Log.e(TAG, "이미 존재 하는 테이블 입니다. ${e.stackTraceToString()}")
        }
    }

    //select
    fun getCheckList(listName: String): List<CheckListEntity> {
        return checkListDao.getCheckList(listName)
    }
    
    fun getCheckListUpdate(listName: String): List<CheckListUpdateEntity> {
        return checkListUpdateDao.getCheckListUpdateDate(listName)
    }

    //update
    fun updateCheckList(
        idx: Long,
        listName: String,
        checkListContent: String,
        restartWeek: MutableSet<MyDayOfWeek>,
        done: Boolean,
        lastUpdatedDate: LocalDateTime
    ) {
        try {
            val checkList = checkListDao.getCheckList(listName)
            if (checkList.isEmpty()) {
                Log.d("updateCheckList", "checkList is Empty so Insert")
                insertCheckList(listName, checkListContent, restartWeek, done, lastUpdatedDate)
            }
            else {
                Log.d("updateCheckList", "checkList update done")
                checkListDao.updateCheckList(
                    idx,
                    listName,
                    checkListContent,
                    restartWeek,
                    done,
                    lastUpdatedDate
                )
            }
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Database sync(Update & Insert) is Failed")
        }
    }

    //delete
    fun deleteDatabase(deleteIndex: Long) {
        try {
            Log.d(TAG, "$deleteIndex index is deleted")
            checkListDao.deleteCheckList(deleteIndex)
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Database Delete is Failed")
        }
    }
}
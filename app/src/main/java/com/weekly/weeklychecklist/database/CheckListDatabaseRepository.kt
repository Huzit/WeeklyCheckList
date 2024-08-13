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
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckListDatabaseRepository @Inject constructor(private val checkListDao: CheckListDao, private val checkListUpdateDao: CheckListUpdateDao) {
    private val TAG = javaClass.simpleName
    private lateinit var db: CheckListDatabase

    //초기화
//    fun initDatabase(context: Context) = CoroutineScope(Dispatchers.IO).launch {
//        db = Room.databaseBuilder(
//            context,
//            CheckListDatabase::class.java,
//            "CheckListDatabase"
//        ).build()
//    }
    
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
        Log.d(javaClass.simpleName, "업데이트 내역 : $checkListUpdateEntity")
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
    ){
        checkListDao.updateCheckList(
            idx,
            listName,
            checkListContent,
            restartWeek,
            done,
            lastUpdatedDate
        )
    }

    //update All
    fun updateCheckListAll(checkList: List<CheckListEntity>){
        checkListDao.test(checkList)
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
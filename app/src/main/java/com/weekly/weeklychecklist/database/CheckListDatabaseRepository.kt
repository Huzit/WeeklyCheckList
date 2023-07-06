package com.weekly.weeklychecklist.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.weekly.weeklychecklist.vm.CheckListInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.time.LocalDate

class CheckListDatabaseRepository(private val context: Context) {
    private lateinit var db: CheckListDatabase
    private lateinit var dao: CheckListDao

    companion object: SingletonHolder<CheckListDatabaseRepository, Context>(::CheckListDatabaseRepository)
    //초기화
    fun initDatabase(){
        db = Room.databaseBuilder(
            context,
            CheckListDatabase::class.java,
            "checklist"
        ).build()
        dao =  db.checklistDao()
    }
    private fun insertDatabase(listName: String, clInfo: List<CheckListInfo>, isUpdated: Boolean, lastUpdatedDate: LocalDate) = CoroutineScope(Dispatchers.IO).launch {
        try{
            dao.insertCheckList(CheckListEntity(listName, clInfo, isUpdated, lastUpdatedDate))
        } catch (e: RuntimeException){
            Log.e("WeeklyCheckList "+javaClass.simpleName, "이미 존재 하는 테이블 입니다.")
        }
    }

    //select
    fun getDatabase(listName: String): CheckListEntity = runBlocking { dao.getCheckList(listName) }

    //update
    fun updateDatabase(listName: String, clInfo: List<CheckListInfo>, isUpdated: Boolean, lastUpdatedDate: LocalDate) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val checkList = dao.getCheckList(listName)
            //빈 경우
            if(checkList == null ){
                insertDatabase(listName, clInfo, isUpdated, lastUpdatedDate)
            }
            //있을 경우
            else{
                checkList.checkLists = clInfo
                checkList.lastUpdatedDate = lastUpdatedDate
                checkList.isUpdated = isUpdated
                dao.updateCheckList(checkList)
            }
        }catch (e: IOException){
            Log.e(javaClass.simpleName, "Database sync(Update & Insert) is Failed")
        }
    }
    //delete
    fun deleteDatabase() = CoroutineScope(Dispatchers.IO).launch {
        try{
            dao.deleteCheckList()
        }catch (e: IOException){
            Log.e(javaClass.simpleName, "Database Delete is Failed")
        }
    }
}
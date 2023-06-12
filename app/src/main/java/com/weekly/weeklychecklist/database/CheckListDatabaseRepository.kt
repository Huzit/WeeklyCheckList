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
import java.lang.RuntimeException

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

    fun insertDatabase(listName: String, clInfo: List<CheckListInfo>) = CoroutineScope(Dispatchers.IO).launch {
        val a = dao.getCheckList(listName)
        try{
            //처음일 때만 insert
            if(a == null){
                dao.insertCheckList(CheckListEntity(listName, clInfo))
            } else{
                throw RuntimeException("이미 존재하는 테이블입니다.")
            }
        } catch (e: RuntimeException){
            Log.e("WeeklyCheckList "+javaClass.simpleName, "이미 존재 하는 테이블 입니다.")
        }
    }

    //select
    fun getDatabase(listName: String): CheckListEntity = runBlocking { dao.getCheckList(listName) }

    //update
    fun updateDatabase(listName: String, clInfo: List<CheckListInfo>) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val _checkList = dao.getCheckList(listName)
            //빈 경우
            if(_checkList == null ){
                insertDatabase(listName, clInfo)
            }
            //있을 경우
            else{
                _checkList.checkLists = clInfo
                dao.updateCheckList(_checkList)
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
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
    //select
    fun getDatabase(): CheckListEntity = runBlocking { dao.getCheckList() }
    //insert & update
    fun updateDatabase(clInfo: List<CheckListInfo>) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val _checkList = dao.getCheckList()
            //빈 경우
            if(_checkList.checkLists.isEmpty()){
                dao.insertCheckList(CheckListEntity(clInfo))
            }
            //있을 경우
            else{
                _checkList.checkLists = clInfo
                dao.updateCheckList(_checkList)
            }
        }catch (e: IOException){
            Log.e(javaClass.simpleName, "Database Update(Insert) is Failed")
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
    //sync
    fun syncWithDB(clInfos: ArrayList<CheckListInfo>) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val dbList = dao.getCheckList()
            if(dbList.checkLists.isNotEmpty()) {
                dbList.checkLists = clInfos
                dao.updateCheckList(dbList)
            }
        }catch(e: IOException){
            Log.e(javaClass.simpleName, "Synchronize is Failed")
        }
    }
}
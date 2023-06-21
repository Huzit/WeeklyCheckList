package com.weekly.weeklychecklist.vm

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import kotlin.properties.Delegates
import com.weekly.weeklychecklist.DayOfWeek as myDayOfWeek
import java.time.DayOfWeek as javaDayOfWeek

@RequiresApi(Build.VERSION_CODES.O)
class CheckListViewModel(): ViewModel() {
    var checkList = mutableStateListOf<CheckListInfo>()
    var listName = mutableStateOf<String>("default")
    var isUpdated: Boolean = false
    var lastUpdatedDate = LocalDate.now()
    lateinit var context: Context

    //SnackBar 메시지 트리거
    val isSwipe = mutableStateOf(false)
    private var checkListId = checkList.size

    fun initContext(context: Context){
        this.context = context
    }
    fun checklistToString(): String {
        val sb = StringBuilder()
        checkList.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

    fun getCheckListId(): Int {
        return checkListId++
    }

    //요일마다 스위치 초기화
    @RequiresApi(Build.VERSION_CODES.O)
    fun switchInitialization(){
        //오늘의 요일
        var today = when(LocalDate.now().dayOfWeek){
            javaDayOfWeek.MONDAY -> myDayOfWeek.월
            javaDayOfWeek.TUESDAY -> myDayOfWeek.화
            javaDayOfWeek.WEDNESDAY -> myDayOfWeek.수
            javaDayOfWeek.THURSDAY -> myDayOfWeek.목
            javaDayOfWeek.FRIDAY -> myDayOfWeek.금
            javaDayOfWeek.SATURDAY -> myDayOfWeek.토
            javaDayOfWeek.SUNDAY -> myDayOfWeek.일
            else -> myDayOfWeek.널
        }

        checkList.forEachIndexed { index, item ->
            //조건1. 요일이 같으면 초기화
            if(item.restartWeek.contains(today) && !isUpdated) {
                item.done = false
                //마지막까지 초기화 했을 시
                if(index == checkList.size - 1)
                    isUpdated = true
            }
            //조건2. 요일이 아닐 때
            else{
                getBetweenWeek(lastUpdatedDate)
            }
        }
    }

    fun getBetweenWeek(lastUpdatedDate: LocalDate): Set<String>{
        val today = LocalDate.now().toString()
        val baseDay = lastUpdatedDate
        val weeks = arrayListOf<String>()
        //lud ~ today 까지의 요일 리턴
        while(baseDay.toString() != today){
            weeks.add(baseDay.dayOfWeek.toString())
            baseDay.plusDays(1)
        }
        return weeks.toSet()
    }
}
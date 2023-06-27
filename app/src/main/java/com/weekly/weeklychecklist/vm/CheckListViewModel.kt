package com.weekly.weeklychecklist.vm

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.lang.RuntimeException
import java.time.DayOfWeek
import java.time.LocalDate
import com.weekly.weeklychecklist.MyDayOfWeek
import java.time.DayOfWeek as javaDayOfWeek

class CheckListViewModel(): ViewModel() {
    var checkList = mutableStateListOf<CheckListInfo>()
    var listName = mutableStateOf<String>("default")
    var isUpdated: Boolean = false
    var lastUpdatedDate = LocalDate.now()
    //SnackBar 메시지 트리거
    val isSwipe = mutableStateOf(false)
    private var checkListId = 0

    fun checklistToString(): String {
        val sb = StringBuilder()
        checkList.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

    fun setCheckListId(size: Int){
        checkListId = size
    }

    fun getCheckListId(): Int {
        //생성 후 DB 저장 전에 튕기면 key 중복 발생
        if(checkList.size-1 == checkListId)
            checkListId+=2
        else
            checkListId++
        Log.d("체크리스트 아이디", checkListId.toString())
        return checkListId
    }

    //요일마다 스위치 초기화
    fun switchInitialization(){
        /***
         * isUpdate는 onDestroy 시 다시 시작하므로 알아서 false
         * 만약, 포그라운드에서 하루 이상이 지난다면 onDestroy를 못 탈수도 있으므로 false
         ***/
        if(lastUpdatedDate.isBefore(LocalDate.now()))
            isUpdated = false
        Log.d("on포그라운드", isUpdated.toString())

        //오늘의 요일
        val today = when(LocalDate.now().dayOfWeek){
            javaDayOfWeek.MONDAY -> MyDayOfWeek.월
            javaDayOfWeek.TUESDAY -> MyDayOfWeek.화
            javaDayOfWeek.WEDNESDAY -> MyDayOfWeek.수
            javaDayOfWeek.THURSDAY -> MyDayOfWeek.목
            javaDayOfWeek.FRIDAY -> MyDayOfWeek.금
            javaDayOfWeek.SATURDAY -> MyDayOfWeek.토
            javaDayOfWeek.SUNDAY -> MyDayOfWeek.일
            else -> MyDayOfWeek.널
        }
        if(lastUpdatedDate == null){
            throw RuntimeException("환아 날짜가 없다")
        }
        val passedWeek = getBetweenWeek(lastUpdatedDate)
        //일주일 이상 지났을 시 전체 초기화
        if(passedWeek.size >= 7){
            checkList.forEach { item ->
                item.done = false
            }
            lastUpdatedDate = LocalDate.now()
            isUpdated = true
            return
        }
        //최근 접속일 이 일주일 미만일 시
        else {
            checkList.forEachIndexed { index, item ->
                //조건1. 같은 요일
                if (item.restartWeek.contains(today) && !isUpdated) {
                    item.done = false
                    isUpdated = true
                }
                //조건2. 다른 요일
                else {
                    item.restartWeek.forEach { week ->
                        if (passedWeek.contains(week)) {
                            item.done = false
                        }
                    }
                    isUpdated = true
                }
                //마지막 일 시
                if (index == checkList.size - 1) {
                    isUpdated = true
                    lastUpdatedDate = LocalDate.now()
                    return
                }
            }
        }
    }
    //오늘 ~ 이전 이전 업데이트 날짜 사이의 요일 구하기
    private fun getBetweenWeek(lastUpdatedDate: LocalDate): Set<MyDayOfWeek>{
        val today = LocalDate.now().toString()
        var mLastUpdatedDate = lastUpdatedDate
        val weeks = arrayListOf<DayOfWeek>()
        val reWeeks = arrayListOf<MyDayOfWeek>()

        //마지막 앱 시작 날짜 부터 오늘 까지 요일 리턴
        while(mLastUpdatedDate.toString() != today){
            weeks.add(mLastUpdatedDate.dayOfWeek)
            mLastUpdatedDate = mLastUpdatedDate.plusDays(1)
        }
        weeks.forEach { week ->
            reWeeks.add(convertDayOfWeekToMyDayOfWeek(week))
        }
        return reWeeks.toSet()
    }
    //java.time.DayOfWeek 를 MyDayOfWeek로 변경
    fun convertDayOfWeekToMyDayOfWeek(weeks: DayOfWeek) = when(weeks){
        DayOfWeek.MONDAY    -> MyDayOfWeek.월
        DayOfWeek.TUESDAY   -> MyDayOfWeek.화
        DayOfWeek.WEDNESDAY -> MyDayOfWeek.수
        DayOfWeek.THURSDAY  -> MyDayOfWeek.목
        DayOfWeek.FRIDAY    -> MyDayOfWeek.금
        DayOfWeek.SATURDAY  -> MyDayOfWeek.토
        DayOfWeek.SUNDAY    -> MyDayOfWeek.일
    }

    fun convertMyDayOfWeekToDayOfWeek(weeks: MyDayOfWeek) = when(weeks){
        MyDayOfWeek.월  -> DayOfWeek.MONDAY
        MyDayOfWeek.화  -> DayOfWeek.TUESDAY
        MyDayOfWeek.수  -> DayOfWeek.WEDNESDAY
        MyDayOfWeek.목  -> DayOfWeek.THURSDAY
        MyDayOfWeek.금  -> DayOfWeek.FRIDAY
        MyDayOfWeek.토  -> DayOfWeek.SATURDAY
        MyDayOfWeek.일  -> DayOfWeek.SUNDAY
        else -> ""
    }
}
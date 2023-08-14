package com.weekly.weeklychecklist.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weekly.weeklychecklist.MyDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDate

class CheckListViewModel(): ViewModel() {
    //체크리스트
    var checkList = mutableStateListOf<CheckListInfo>()
    //체크리스트의 이름
    var listName = mutableStateOf<String>("default")
    //요일 초기화 됬는 지
    var isUpdated: Boolean = false
    //마지막으로 업데이트 된 날짜
    var lastUpdatedDate: LocalDate = LocalDate.now()
    //SnackBar 메시지 트리거
    val isSwipe = mutableStateOf(false)
    //onResume ReComposition Trigger
    var restartMainActivity: Boolean = false
    //무결성 검증을 위한 ID 리스트
    val idList = mutableMapOf<Int, Boolean>()

    var swipRemoveFlag = MutableLiveData(true)

    private var checkListId = 0

    fun checklistToString(): String {
        val sb = StringBuilder()
        checkList.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

    fun setCheckListId(id: Int){
        checkListId = id
    }

    fun getCheckListId(): Int {
        //무결성 검증 후 리턴
        if(idList.contains(checkListId)){
            //중복일 시 증가
            ++checkListId
        }
        idList[checkListId] = true
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
        Log.d("업데이트 됨?", isUpdated.toString())

        val passedWeek = getBetweenWeek(lastUpdatedDate)

        Log.d("지난날짜", passedWeek.size.toString())

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
                if (!isUpdated) {
                    item.restartWeek.forEach { week ->
                        if (passedWeek.contains(week)) {
                            item.done = false
                        }
                    }
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
        val today = LocalDate.now()
        var mLastUpdatedDate = lastUpdatedDate
        val weeks = arrayListOf<DayOfWeek>()
        val reWeeks = arrayListOf<MyDayOfWeek>()

        //마지막 앱 시작 날짜 부터 오늘 까지 요일 리턴
        while(mLastUpdatedDate.isBefore(today)){
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
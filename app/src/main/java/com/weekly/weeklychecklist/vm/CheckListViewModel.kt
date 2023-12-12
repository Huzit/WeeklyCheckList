package com.weekly.weeklychecklist.vm

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class CheckListViewModel() : ViewModel() {
    //swipToDismiss 롤백 트리거
    var isSwipToDeleteCancel: Boolean = false
    var checkList = mutableStateListOf<CheckListEntity>()
    var checkListBackUp = ArrayList<CheckListEntity>()
    var listName = mutableStateOf<String>("default")
    var checkListUpdate = ArrayList<CheckListUpdateEntity>()
    var lastUpdatedDate: LocalDate = LocalDate.now()
    val isSwipe = mutableStateOf(false)
    val idList = mutableMapOf<Int, Boolean>()

    //onResume ReComposition Trigger
    var restartMainActivity: Boolean = false
    private val checkListRepository = CheckListDatabaseRepository.getInstance()


    private var checkListId = 0
    
    fun checklistToString(): String {
        val sb = StringBuilder()
        checkList.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

    fun getCheckLists(){
        CoroutineScope(Dispatchers.IO).launch {
            //유저 체크리스트를 관리하는 DB
            val clList = getCheckList("default")
            //마지막으로 업데이트 된 날짜를 관리하는 DB
            val clUpdate = getCheckListUpdate("default")
            //DB get
            if (clList.isNotEmpty()) {
                Log.d(javaClass.simpleName, "clList size == ${clList.size}, startRow == ${clList.first()}")
                checkList = clList.toMutableStateList()
                checkListBackUp = clList
                checkListUpdate = clUpdate
            }
        }
    }

    
    private fun getCheckList(
        listName: String
    ): ArrayList<CheckListEntity> = ArrayList(checkListRepository.getCheckList(listName))
    
    private fun getCheckListUpdate(
        listName: String
    ): ArrayList<CheckListUpdateEntity> = ArrayList(checkListRepository.getCheckListUpdate(listName))

    fun insertCheckList(
        listName: String,
        checkListContent: String,
        restartWeek: MutableSet<MyDayOfWeek>,
        done: Boolean,
        lastUpdatedDate: LocalDateTime
    ) = CoroutineScope(Dispatchers.IO).launch {
        checkListRepository.insertCheckList(
            listName,
            checkListContent,
            restartWeek,
            done,
            lastUpdatedDate
        )
    }

    fun updateCheckList(
        idx: Long,
        listName: String,
        checkListContent: String,
        restartWeek: MutableSet<MyDayOfWeek>,
        done: Boolean,
        lastUpdatedDate: LocalDateTime
    ) = CoroutineScope(Dispatchers.IO).launch {
        checkListRepository.updateCheckList(
            idx,
            listName,
            checkListContent,
            restartWeek,
            done,
            lastUpdatedDate
        )
    }

    fun updateIfDone(){
        //수정 후
        val sortedCheckList = checkList.associateBy { it.idx }.toMutableMap()
        //DB에서 불러온 데이터
        val sortedCheckListBackup = checkListBackUp.associateBy { it.idx }
        //변경된 적 없는(업데이트 할 필요 없는) 항목 제거
        for(i in sortedCheckListBackup){
            if(sortedCheckList.containsKey(i.key)){
                val list = sortedCheckList[i.key]
                if(list != null
                    && list.checklistContent == i.value.checklistContent
                    && list.restartWeek == i.value.restartWeek
                    && list.done == i.value.done) {
                    //TODO 정확히 지워지는지 테스트
                    sortedCheckList.remove(i.key)
                }
            }
        }
        //나머지 업데이트
        for(i in sortedCheckList) {
            val list = i.value
            //TODO 업데이트 되는지 테스트
            updateCheckList(
                idx = list.idx,
                listName = list.listName,
                checkListContent = list.checklistContent,
                restartWeek = list.restartWeek,
                done = list.done,
                lastUpdatedDate = LocalDateTime.now()
            )
        }
    }
    
    private fun updateCheckListUpdate(
        checkListUpdateEntity: CheckListUpdateEntity
    ) = CoroutineScope(Dispatchers.IO).launch {
        checkListRepository.updateCheckListUpdate(
            checkListUpdateEntity
        )
    }

    //요일마다 스위치 초기화
    fun switchInitialization() {
        /***
         * isUpdate는 onDestroy 시 다시 시작하므로 알아서 false
         * 만약, 포그라운드에서 하루 이상이 지난다면 onDestroy를 못 탈수도 있으므로 false
         ***/
        if (lastUpdatedDate.isBefore(LocalDate.now()))
            checkListUpdate[0].isUpdate = false
        val passedWeek = getBetweenDate(lastUpdatedDate)

        //일주일 이상 지났을 시 전체 초기화
        if (passedWeek.size >= 7) {
            checkList.forEach { item ->
                item.done = false
            }
            lastUpdatedDate = LocalDate.now()
            checkListUpdate[0].isUpdate = true
            checkListUpdate[0].registerTime = LocalDateTime.now()
            //TODO Update 통신 필요
            updateCheckListUpdate(checkListUpdate[0])
            return
        }
        //최근 접속일 이 일주일 미만일 시
        else {
            checkList.forEachIndexed { index, item ->
                if (!checkListUpdate[0].isUpdate) {
                    item.restartWeek.forEach { week ->
                        if (passedWeek.contains(week)) {
                            item.done = false
                        }
                    }
                    //마지막 일 시
                    if (index == checkList.size - 1) {
                        checkListUpdate[0].isUpdate = true
                        lastUpdatedDate = LocalDate.now()
                        updateCheckListUpdate(checkListUpdate[0])
                        return
                    }
                }
            }
        }
    }

    //오늘 ~ 이전 이전 업데이트 날짜 사이의 요일 구하기
    private fun getBetweenDate(lastUpdatedDate: LocalDate): Set<MyDayOfWeek> {
        val today = LocalDate.now()
        var mLastUpdatedDate = lastUpdatedDate
        val weeks = arrayListOf<DayOfWeek>()
        val reWeeks = arrayListOf<MyDayOfWeek>()

        //마지막 앱 시작 날짜 부터 오늘 까지 요일 리턴
        while (mLastUpdatedDate.isBefore(today)) {
            weeks.add(mLastUpdatedDate.dayOfWeek)
            mLastUpdatedDate = mLastUpdatedDate.plusDays(1)
        }
        weeks.forEach { week ->
            reWeeks.add(convertDayOfWeekToMyDayOfWeek(week))
        }
        return reWeeks.toSet()
    }
    //java.time.DayOfWeek 를 MyDayOfWeek로 변경
    private fun convertDayOfWeekToMyDayOfWeek(weeks: DayOfWeek) = when (weeks) {
        DayOfWeek.MONDAY -> MyDayOfWeek.월
        DayOfWeek.TUESDAY -> MyDayOfWeek.화
        DayOfWeek.WEDNESDAY -> MyDayOfWeek.수
        DayOfWeek.THURSDAY -> MyDayOfWeek.목
        DayOfWeek.FRIDAY -> MyDayOfWeek.금
        DayOfWeek.SATURDAY -> MyDayOfWeek.토
        DayOfWeek.SUNDAY -> MyDayOfWeek.일
    }

    fun convertMyDayOfWeekToDayOfWeek(weeks: MyDayOfWeek) = when (weeks) {
        MyDayOfWeek.월 -> DayOfWeek.MONDAY
        MyDayOfWeek.화 -> DayOfWeek.TUESDAY
        MyDayOfWeek.수 -> DayOfWeek.WEDNESDAY
        MyDayOfWeek.목 -> DayOfWeek.THURSDAY
        MyDayOfWeek.금 -> DayOfWeek.FRIDAY
        MyDayOfWeek.토 -> DayOfWeek.SATURDAY
        MyDayOfWeek.일 -> DayOfWeek.SUNDAY
        else -> ""
    }
}
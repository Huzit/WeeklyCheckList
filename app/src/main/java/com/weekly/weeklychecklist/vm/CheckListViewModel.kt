package com.weekly.weeklychecklist.vm

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity
import com.weekly.weeklychecklist.util.CheckListUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class CheckListViewModel() : ViewModel() {
    private val TAG = "CheckListViewModel"
    //swipToDismiss 롤백 트리거
    var isSwipToDeleteCancel: Boolean = false
    //스플래시 화면 true : 지남, false : 안지남
    var isSplashed = false
    val onResumeRefreshed = mutableStateOf(false)
    var checkList = mutableStateListOf<CheckListEntity>()
//    var customToggleRefreshingOnResumeState = mutableStateOf(false)
    var customToggleRefreshingDraggableState = mutableStateOf(false)
    var listName = mutableStateOf<String>("default")
    var checkListUpdate = ArrayList<CheckListUpdateEntity>()
    val isSwipe = mutableStateOf(false)

    private val checkListRepository = CheckListDatabaseRepository.getInstance()
    var isFinished = false

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
                checkListUpdate = clUpdate
                Log.d("Get CheckLists", clList.toString())
            }
            isFinished = true
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
    //스와이프 해도 안바뀌는건 이 부분 오류
    fun updateCheckListAll() = CoroutineScope(Dispatchers.IO).launch {
        Log.d("Updated CheckLists", checkList.toString())
        if(checkList.isNotEmpty()){
            checkList.forEach{ list ->
                checkListRepository.updateCheckList(
                    list.idx,
                    list.listName,
                    list.checklistContent,
                    list.restartWeek,
                    list.done,
                    list.registerTime
                )
            }
        }
    }

    fun updateTest(){
        CoroutineScope(Dispatchers.IO).launch {
            checkList.forEach {
                Log.d("디버그 | 체크리스트", it.toString())
            }
            checkListRepository.updateCheckListAll(checkList)
        }
    }

    //TODO 로직 수정 필요함
/*    fun updateIfDone(){
        //수정 후
        val checkListMap = checkList.associateBy { it.idx }.toMutableMap()
        Log.d("updateIfDone", checkListMap.toString())
        checkListMap[0]?.done = true
        //DB에서 불러온 데이터
        Log.d("updateIfDone", checkListBackUp.toString())
        //변경된 적 없는(업데이트 할 필요 없는) 항목 제거
        for(i in checkListBackUp){
            if(checkListMap.containsKey(i.key)){
                val list = checkListMap[i.key]
                if(list != null
                    && list.checklistContent == i.value.checklistContent
                    && list.restartWeek == i.value.restartWeek
                    && list.done == i.value.done) {
                    Log.d("updateIfDone", "지운 항목 : $list")
                    checkListMap.remove(i.key)
                }
            }
        }
        //나머지 업데이트
        for(i in checkListMap) {
            val list = i.value
            Log.d(TAG, "update : $list")
            updateCheckList(
                idx = list.idx,
                listName = list.listName,
                checkListContent = list.checklistContent,
                restartWeek = list.restartWeek,
                done = list.done,
                lastUpdatedDate = LocalDateTime.now()
            )
        }
    }*/
    //해당 체크리스트의 수정일 최초 등록
    fun insertCheckListUpdate(
        listName: String,
        isUpdated: Boolean,
        registerTime: LocalDateTime
    ) = CoroutineScope(Dispatchers.IO).launch {
        if(checkListUpdate.isNotEmpty()){
            checkListUpdate.forEach {checkList ->
                if(checkList.listName == listName){
                    return@launch
                }
            }
            checkListRepository.insertCheckListUpdate(
                listName,
                isUpdated,
                registerTime
            )
        } else{
            checkListRepository.insertCheckListUpdate(
                listName,
                isUpdated,
                registerTime
            )
        }
    }

    fun updateCheckListUpdate(
        checkListUpdateEntity: CheckListUpdateEntity,
    ) = CoroutineScope(Dispatchers.IO).launch {
        checkListRepository.updateCheckListUpdate(
            checkListUpdateEntity
        )
    }

    fun forceRecomposition(){
        checkList = ArrayList(checkList.toList()).toMutableStateList()
    }

    //요일마다 스위치 초기화
    fun switchInitialization(context: Context) {
        /***
         * isUpdate는 onDestroy 시 다시 시작하므로 알아서 false
         * 만약, 포그라운드에서 하루 이상이 지난다면 onDestroy를 못 탈수도 있으므로 false
         ***/
        var lastUpdatedDate: LocalDate = LocalDate.now()

        Log.d("CheckListViewModel", "checkList isEmpty = ${checkListUpdate.isEmpty()}")

        if (checkListUpdate.isNotEmpty()) {
            lastUpdatedDate = checkListUpdate[0].registerTime.toLocalDate()
            //오늘 이전일 시
            if (lastUpdatedDate.isBefore(LocalDate.now()))
                checkListUpdate[0].isUpdate = false
            val passedWeek = getBetweenDate(lastUpdatedDate)
            if(passedWeek.isNotEmpty())
                Toast.makeText(
                    context,
                    if(passedWeek.size == 7)
                        "일주일이 지났습니다"
                    else
                        "${passedWeek.toString().filter { it != '[' && it != ']' }}요일이 지났습니다",
                    Toast.LENGTH_LONG).show()

        //일주일 이상 지났을 시 전체 초기화
            if (passedWeek.size >= 7) {
                Log.d(javaClass.simpleName, "7일 이상 경과, 전체 초기화")
                checkList.forEach { item ->
                    item.done = false
                }
                checkListUpdate[0].apply {
                    isUpdate = true
                    registerTime = LocalDateTime.now()
                }
                Log.d(javaClass.simpleName, "업데이트 하는 엔티티 : $checkListUpdate")
                updateCheckListUpdate(checkListUpdate[0])
                lastUpdatedDate = LocalDate.now()
                return
            }
            //최근 접속일 이 일주일 미만일 시
            else {
                Log.d(javaClass.simpleName, "일주일 미만, 부분 초기화")
                checkList.forEachIndexed { index, item ->
                    if (!checkListUpdate[0].isUpdate) {
                        item.restartWeek.forEach { week ->
                            if (passedWeek.contains(week)) {
                                item.done = false
                                Log.d(javaClass.simpleName, "초기화 대상 : $item")
                            }
                        }
                        //마지막 일 시
                        if (index == checkList.size - 1) {
                            Log.d(javaClass.simpleName, "초기화 요일 정보 :${checkListUpdate}")
                            checkListUpdate[0].apply {
                                isUpdate = true
                                registerTime = LocalDateTime.now()
                            }
                            Log.d(javaClass.simpleName, "업데이트 하는 엔티티 : $checkListUpdate")
                            updateCheckListUpdate(checkListUpdate[0])
                            lastUpdatedDate = LocalDate.now()
                            return
                        }
                    }
                }
            }
        }
    }

    //지난 날짜의 요일 리턴
    private fun getBetweenDate(lastUpdatedDate: LocalDate): Set<MyDayOfWeek> {
        val today = LocalDate.now()
        var mLastUpdatedDate = lastUpdatedDate
        val weeks = arrayListOf<DayOfWeek>()
        val reWeeks = arrayListOf<MyDayOfWeek>()
        val util = CheckListUtils()
        Log.d(javaClass.simpleName, "시작일 : $mLastUpdatedDate / 종료일 : $today")

        //마지막 앱 시작 날짜 부터 어제까지 요일 리턴
        while (mLastUpdatedDate.isBefore(today)) {
            weeks.add(mLastUpdatedDate.dayOfWeek)
            mLastUpdatedDate = mLastUpdatedDate.plusDays(1)
        }
        weeks.forEach { week ->
            reWeeks.add(util.convertDayOfWeekToMyDayOfWeek(week))
        }
        Log.d(javaClass.simpleName, "PassedWeek : ${reWeeks.toSet()}")
        return reWeeks.toSet()
    }

    fun deleteCheckList(deleteIndex: Long) = CoroutineScope(Dispatchers.IO).launch {
        checkListRepository.deleteDatabase(deleteIndex)
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

//    fun checkListProperties(fromIndex: Int, toIndex: Int) {
//        cl.value = cl.value.apply {
//            val text = this[fromIndex]
//            this[fromIndex] = this[toIndex]
//            this[toIndex] = text
//        }
//    }
}
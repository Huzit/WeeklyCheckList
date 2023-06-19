package com.weekly.weeklychecklist.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CheckListViewModel: ViewModel() {
    var checkList = mutableStateListOf<CheckListInfo>()
    var listName = mutableStateOf<String>("default")
    //SnackBar 메시지 트리거
    val isSwipe = mutableStateOf(false)
    private var checkListId = checkList.size

    fun checklistToString(): String {
        val sb = StringBuilder()
        checkList.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

    fun getCheckListId(): Int = checkListId++
}
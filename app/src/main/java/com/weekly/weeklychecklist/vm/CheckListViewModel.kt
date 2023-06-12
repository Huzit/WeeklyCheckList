package com.weekly.weeklychecklist.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CheckListViewModel: ViewModel() {
    var checklist = mutableStateListOf<CheckListInfo>()
    var listName = mutableStateOf<String>("default")
    val isSwipe = mutableStateOf(false)

    fun checklistToString(): String {
        val sb = StringBuilder()
        checklist.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }
}
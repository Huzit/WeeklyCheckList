package com.weekly.weeklychecklist.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CheckListViewModel: ViewModel() {
    val checklist = mutableStateListOf<CheckListInfo>()
    val garbage = mutableStateListOf<CheckListInfo>()
    val isSwipe = mutableStateOf(false)

    fun checklistToString(): String {
        val sb = StringBuilder()
        checklist.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

    fun garbageToString(): String{
        val sb = StringBuilder()
        garbage.forEach {
            sb.append("$it ")
        }
        return sb.toString()
    }

}
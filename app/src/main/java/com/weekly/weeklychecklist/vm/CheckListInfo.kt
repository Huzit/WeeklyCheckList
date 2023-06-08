package com.weekly.weeklychecklist.vm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.weekly.weeklychecklist.DayOfWeek
import kotlinx.coroutines.internal.SynchronizedObject

data class CheckListInfo(
    //할 일
    var checklistContent: String,
    //초기화 요일
    var restartWeek: Set<DayOfWeek>,
    //수행여부
    var done: Boolean = false,
    //애니메이션 visivle
    var visibility: MutableState<Boolean> = mutableStateOf(true)
)
package com.weekly.weeklychecklist.vm

import com.weekly.weeklychecklist.DayOfWeek

data class CheckListInfo(
    //할 일
    var checkListContent: String,
    //초기화 요일
    var restartWeek: Set<DayOfWeek>
)
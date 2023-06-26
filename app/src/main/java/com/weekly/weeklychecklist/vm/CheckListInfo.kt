package com.weekly.weeklychecklist.vm

import com.weekly.weeklychecklist.MyDayOfWeek

data class CheckListInfo(
    //고유 ID
    var id: Int,
    //할 일
    var checklistContent: String,
    //초기화 요일
    var restartWeek: Set<MyDayOfWeek>,
    //수행여부
    var done: Boolean = false,
)
{
}
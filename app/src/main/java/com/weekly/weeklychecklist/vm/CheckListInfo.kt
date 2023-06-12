package com.weekly.weeklychecklist.vm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.weekly.weeklychecklist.DayOfWeek
import kotlinx.coroutines.internal.SynchronizedObject
import java.io.Serializable

data class CheckListInfo(
    //할 일
    var checklistContent: String,
    //초기화 요일
    var restartWeek: Set<DayOfWeek>,
    //수행여부
    var done: Boolean = false,
    //애니메이션 visivle
    var visibility: MutableState<Boolean> = mutableStateOf(true)
)//: Serializable
{
    companion object {
        fun fromJson(string: String?): List<CheckListInfo>? {
            val listType = object: TypeToken<List<CheckListInfo>>(){}.type
            if( string.isNullOrEmpty()){
                return null
            }
            return GsonBuilder().create().fromJson(string, listType)
        }
    }
}
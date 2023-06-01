package com.weekly.weeklychecklist.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class CheckListViewModel: ViewModel() {
    val checkList = mutableStateListOf<CheckListInfo>()
}
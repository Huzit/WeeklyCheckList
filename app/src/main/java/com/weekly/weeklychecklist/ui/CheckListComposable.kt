package com.weekly.weeklychecklist.ui

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun listTodo(
    clVM: CheckListViewModel,
    openIndex: MutableState<Int> = mutableStateOf(-1),
    openFlag: MutableState<Boolean> = mutableStateOf(false)
): Pair<MutableState<Boolean>, MutableState<Int>> {
    val listState = rememberLazyListState()
    val checkList by clVM.checkList.collectAsState()
    var dialogVisible by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(-1) }

    val dragDropState = rememberDragDropStste(listState) { fromIndex, toIndex ->
        runBlocking {
            clVM.changeListElement(fromIndex, toIndex)
            Log.d("디버그 리스트", checkList.toString())
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .dragContainer(dragDropState),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            count = checkList.size,
        ) { index ->
            DraggableItem(dragDropState = dragDropState, index = index) { _ ->
                //체크리스트(스와이프) 정의
                ChecklistSwipable(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            openIndex.value = index
                            openFlag.value = !openFlag.value
                        }
                        .animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = LinearOutSlowInEasing,
                            )
                        ),
                    entity = checkList[index],
                    itemIndex = index,
                    clViewModel = clVM
                ) {
                    currentIndex = index
                    dialogVisible = true
                }
            }
        }
    }
    if (dialogVisible) {
        CustomAlertDialog(
            message = "삭제하시겠습니까?",
            positiveEvent = {
                CoroutineScope(Dispatchers.Default).launch {
                    //너무 빨리 삭제되면 swipe 애니메이션이 제대로 출력 안됨
                    val current = checkList[currentIndex]
                    checkList.remove(current)
                    clVM.deleteCheckList(current.idx)
//                    delay(500L)
                }
                //롤백 트리거
                clVM.isSwipToDeleteCancel = true
                clVM.isSwipe.value = true
                dialogVisible = false
            },
            negativeEvent = {
                clVM.isSwipToDeleteCancel = true
                dialogVisible = false
            })
    }
    return Pair(openFlag, openIndex)
}
package com.weekly.weeklychecklist.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class DragDropState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit //이동 이벤트 콜백
) {
    /**드래그 중인 아이템 인덱스*/
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    /**스크롤 채널*/
    internal var scrollChannel = Channel<Float>()

    /**드래그 중인 아이템 Y축 이동 값*/
    private var draggingItemDraggedDelta by mutableStateOf(0f)

    /**드래그 중인 아이템 초기 옵셋*/
    private var draggingItemInitialOffset by mutableStateOf(0)

    /**드래그 중인 아이템 레이아웃 정보*/
    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == draggingItemIndex }

    /**드래그 중인 아이템 오프셋*/
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
        } ?: 0f

    /**직전 드래그 아이템 인덱스*/
    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    /**직전 드래그 아이템 옾셋*/
    private var previousItemOffset = Animatable(0f)

    /**드래그 시작*/
    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..item.offsetEnd
            }?.also {
                draggingItemIndex = it.index
                draggingItemInitialOffset = it.offset
            }
    }

    /**드래그 종료*/
    internal fun onDragInterrupted() {
        if (draggingItemIndex != null) {
            previousIndexOfDraggedItem = draggingItemIndex
            val startOffset = draggingItemOffset

            scope.launch {
                //패러미터로 넘긴 '값'을 previousItemOffset에 할당
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    0f,
                    spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f)
                )
                previousIndexOfDraggedItem = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemIndex = null
        draggingItemInitialOffset = 0
    }

    /**드래그 중*/
    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset.y
        val draggingItem = draggingItemLayoutInfo ?: return
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffset = startOffset + draggingItem.size
        val middleOffset = startOffset + (endOffset - startOffset) / 2f
        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            //오프셋 범위 내면서 자기자신이 아닐 때
            middleOffset.toInt() in item.offset..item.offsetEnd && draggingItem.index != item.index
        }
        if (targetItem != null) {
            //일반적인 상황
            val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
                draggingItem.index
                //드래그 중인 아이템이 지가 첫번째일 때
            } else if (draggingItem.index == state.firstVisibleItemIndex) {
                targetItem.index
            } else {
                null
            }
            //위치 이동 이벤트
            if (scrollToIndex != null) {
                scope.launch {
                    state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                    onMove.invoke(draggingItem.index, targetItem.index)
                }
            } else {
                onMove.invoke(draggingItem.index, targetItem.index)
            }
            draggingItemIndex = targetItem.index
        } else {
            //오버스크롤 시
            val overscroll = when {
                draggingItemDraggedDelta > 0 -> (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(
                    0f
                )

                draggingItemDraggedDelta < 0 -> (startOffset - state.layoutInfo.viewportStartOffset).coerceAtLeast(
                    0f
                )

                else -> 0f
            }
            if (overscroll != 0f)
                scrollChannel.trySend(overscroll)
        }
    }
}

/** Modifier.PointerInput 초기화 */
fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
    return pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset)
            },
            onDragStart = { offset -> dragDropState.onDragStart(offset) },
            onDragEnd = { dragDropState.onDragInterrupted() },
            onDragCancel = { dragDropState.onDragInterrupted() },
        )
    }
}

/**DragDropState 리컴포지션 방지용 remember*/
@Composable
fun rememberDragDropStste(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragDropState {
    val scope = rememberCoroutineScope()
    val state = remember(lazyListState) {
        DragDropState(
            state = lazyListState,
            onMove = onMove,
            scope = scope
        )
    }

    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
/** Items에 드래그앤드랍 이벤트를 설정하는 컴포저블 */
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(isDragging: Boolean) -> Unit
) {
    val dragging = index == dragDropState.draggingItemIndex
    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.draggingItemOffset
            }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.draggingItemOffset
            }
    } else
        Modifier.animateItemPlacement()
    Column(modifier = modifier.then(draggingModifier)) {
        content(dragging)
    }

}


//옾셋 끝을 위한 확장 변수
val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size

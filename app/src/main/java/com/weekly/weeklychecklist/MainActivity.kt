package com.weekly.weeklychecklist

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.ui.ChecklistSwipable
import com.weekly.weeklychecklist.ui.ChecklistWriteBoard
import com.weekly.weeklychecklist.ui.CustomSnackBar
import com.weekly.weeklychecklist.ui.theme.AmbientGray
import com.weekly.weeklychecklist.ui.theme.Red2
import com.weekly.weeklychecklist.ui.theme.Red4
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.security.SecureRandom
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    val clVM: CheckListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyChecklistApp(this)
        }
        //DB init
        val db = CheckListDatabaseRepository.getInstance(this)
        db.initDatabase()
        //get
        if (db.getDatabase("default") != null) {
            clVM.checkList = db.getDatabase("default").checkLists.toMutableStateList()
            db.getDatabase("default")//.checkLists
        } else {
            db.insertDatabase(clVM.listName.value, clVM.checkList)
        }
    }

    override fun onPause() {
        super.onPause()
        CheckListDatabaseRepository.getInstance(this)
            .updateDatabase(clVM.listName.value, clVM.checkList)
    }

    override fun onStop() {
        super.onStop()
        CheckListDatabaseRepository.getInstance(this)
            .updateDatabase(clVM.listName.value, clVM.checkList)
    }

    override fun onDestroy() {
        super.onDestroy()
        CheckListDatabaseRepository.getInstance(this)
            .updateDatabase(clVM.listName.value, clVM.checkList)
    }
}

//앱 전체 컴포저블
@Composable
fun WeeklyChecklistApp(main: MainActivity) {
    val cornerSize = 7
    val boxHeight = LocalConfiguration.current.screenHeightDp.minus(50).dp
    val clVM = viewModel<CheckListViewModel>()

    val backgroundTouchEvent = {
        clVM.isSwipe.value = false
    }

    WeeklyCheckListTheme {
        //뒷 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Red4,
                    shape = RoundedCornerShape(cornerSize, cornerSize, 0, 0)
                )
                .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                    backgroundTouchEvent()
                },
            contentAlignment = Alignment.BottomCenter
        )
        {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                //타이틀
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "나만의 계획표",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                //격자
                Spacer(
                    modifier = Modifier
                        .size(1.dp)
                        .weight(1f)
                )
                //메인 컨텐츠
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(boxHeight)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(cornerSize, cornerSize, 0, 0)
                        )
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ListTodo(context = main)
                }
            }
            FloatingActions(main)
            CustomSnackBar(
                visible = clVM.isSwipe.value,
                text = "삭제되었습니다",
                //자동 종료
                launchedEffect = {
                    clVM.isSwipe.value = false
                }
            )
        }
    }
}

fun <T> MutableList<T>.move(fromIdx: Int, toIdx: Int){
    if(toIdx > fromIdx)
        for (i in fromIdx until toIdx)
            this[i] = this[i + 1].also { this[i + 1] = this[i] }
    else
        for (i in fromIdx downTo  toIdx)
            this[i] = this[i - 1].also { this[i - 1] = this[i] }

}
//투두 리스트 리사이클러뷰
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListTodo(context: Context) {
    val clVM = viewModel<CheckListViewModel>()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //리사이클러뷰
        items(
            count = clVM.checkList.size,
            key = { item: Int -> clVM.checkList[item].id },
        ) { item ->
            val currentItem by rememberUpdatedState(newValue = clVM.checkList[item])
            //체크리스트(스와이프) 정의
            ChecklistSwipable(
                modifier = Modifier.animateItemPlacement(),
                text = currentItem.checklistContent,
                done = currentItem.done,
                index = item
            ) {
                CoroutineScope(Dispatchers.Default).launch {
                    //너무 빨리 삭제되면 swipe 애니메이션이 제대로 출력 안됨
                    delay(300L)
                    //스와이프 시 삭제
                    clVM.checkList.remove(currentItem)
                    clVM.isSwipe.value = true
                }
            }
        }
    }
}

//플로팅 버튼
@Composable
fun FloatingActions(context: Context) {
    var isPressed by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 10.dp, end = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        FloatingActionButton(
            modifier = Modifier.size(70.dp),
            backgroundColor = Red2,
            onClick = {
                isPressed = !isPressed
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                tint = Color.White,
                contentDescription = "Add"
            )
        }
    }
    //작성보드 뒷 배경
    if (isPressed)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AmbientGray)
                .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                    isPressed = false
                },
        )
    val halfHeight = LocalConfiguration.current.screenHeightDp / 2
    AnimatedVisibility(
        visible = isPressed,
        modifier = Modifier.height(350.dp),
        enter = slideIn(initialOffset = { IntOffset(0, halfHeight) }) + fadeIn(initialAlpha = 0f),
        exit = slideOut(
            animationSpec = TweenSpec(100, 100, FastOutLinearInEasing),
            targetOffset = { IntOffset(0, halfHeight) }
        ) +
                fadeOut(
                    animationSpec = TweenSpec(100, 100, FastOutLinearInEasing),
                    targetAlpha = 0f
                ) //속도 더 빠르
    ) {
        ChecklistWriteBoard(main = context) {
            //확인 클릭 시
            isPressed = false
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {
    WeeklyChecklistApp(MainActivity())
}
package com.weekly.weeklychecklist

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.weekly.weeklychecklist.vm.CheckListInfo
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val clVM: CheckListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyChecklistApp(this)
        }
        //DB init
        CheckListDatabaseRepository.getInstance(this).initDatabase()
        //get
        clVM.checklist = CheckListDatabaseRepository
            .getInstance(this)
            .getDatabase()
            .checkLists
            .toMutableStateList()
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
                Spacer( modifier = Modifier
                    .size(1.dp)
                    .weight(1f) )
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
                    ListTodo()
                }
            }
            FloatingActions(main)
            //빠르게 확인 누르면 추가 안되는 버그 있음
            CustomSnackBar(
                visible = clVM.isSwipe.value,
                text = "삭제되었습니다",
                //클릭 이벤트
                onClick = {
                    clVM.isSwipe.value = false
                    val item = clVM.garbage.removeLast()
                    clVM.checklist.add(item)
                    Log.d("FFFFSize", "checklist size : ${clVM.checklist.size} // garbage size : ${clVM.garbage.size}")
                },
                //자동 종료
                launchedEffect = {
                    clVM.isSwipe.value = false
                }
            )
        }
    }
}
//투두 리스트 리사이클러뷰
@Composable
fun ListTodo() {
    val clVM =viewModel<CheckListViewModel>()
    val cl = remember { clVM.checklist }
    val garbage = remember{ clVM.garbage }
    val du = 200
    val context = LocalContext.current.applicationContext

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        //리사이클러뷰
        itemsIndexed(
            items = cl,
            key = {
                index: Int, item: CheckListInfo -> item.hashCode()
            }
        ){ x, item ->
            //삭제 시 fadeOut 애니메이션
            AnimatedVisibility(
                visible = item.visibility.value,
                exit = fadeOut(animationSpec = TweenSpec(du, 300, FastOutLinearInEasing))
            ) {
                //체크리스트(스와이프) 정의
                ChecklistSwipable(text = cl[x].checklistContent, done = cl[x].done, flag = item.visibility.value){
                    item.visibility.value = !clVM.isSwipe.value
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(du+300L)
                        //스와이프 시 삭제
                        cl.remove(item)
                        garbage.add(item)
                        clVM.isSwipe.value = true
                    }
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
                    targetOffset = { IntOffset(0, halfHeight)}
                ) +
                fadeOut(
                    animationSpec = TweenSpec(100, 100, FastOutLinearInEasing),
                    targetAlpha = 0f
                ) //속도 더 빠르
    ) {
        ChecklistWriteBoard(){
            isPressed = false
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {
    WeeklyChecklistApp(MainActivity())
}
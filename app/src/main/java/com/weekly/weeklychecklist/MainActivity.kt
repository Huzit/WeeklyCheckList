package com.weekly.weeklychecklist

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.DialogCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.ui.ChecklistSwipable
import com.weekly.weeklychecklist.ui.CustomSnackBar
import com.weekly.weeklychecklist.ui.checkListWriteBoardWithBackGround
import com.weekly.weeklychecklist.ui.theme.Red2
import com.weekly.weeklychecklist.ui.theme.Red4
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val clVM: CheckListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyChecklistApp(context = this, clVM = clVM)
        }

        //DB init
        val db = CheckListDatabaseRepository.getInstance(this)
        db.initDatabase()
        val default = db.getDatabase("default")

        //DB get
        if (default != null) {
            clVM.apply {
                checkList = default.checkLists.toMutableStateList()
                isUpdated = default.isUpdated ?: false
                lastUpdatedDate = default.lastUpdatedDate

                checkList.forEach { list ->
                    idList.add(list.id)
                }

                if (checkList.size == 0)
                    setCheckListId(0)
                else
                    setCheckListId(checkList.last().id)
            }
        }
    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        //요일 지나면 스위치 초기화
        clVM.switchInitialization()
        //포그라운드시 메인 액티비티 강제 리컴포지션 (추후 반드시 수정할 것, 매우매우 잘못된 방식 이라고 생각!!!)
        if (clVM.restartMainActivity) {
            clVM.restartMainActivity = false
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    //종료 시 자동 저장
    override fun onPause() {
        clVM.restartMainActivity = true
        CheckListDatabaseRepository.getInstance(this)
            .updateDatabase(
                clVM.listName.value,
                clVM.checkList,
                clVM.isUpdated,
                clVM.lastUpdatedDate
            )
        super.onPause()
    }

    override fun onDestroy() {
        CheckListDatabaseRepository.getInstance(this)
            .updateDatabase(
                clVM.listName.value,
                clVM.checkList,
                clVM.isUpdated,
                clVM.lastUpdatedDate
            )
        super.onDestroy()
    }
}

//앱 전체 컴포저블
@Composable
fun WeeklyChecklistApp(context: MainActivity, clVM: CheckListViewModel) {
    val cornerSize = 7
    val boxHeight = LocalConfiguration.current.screenHeightDp.minus(50).dp
    //Custom SnackBar 트리거
    val backgroundTouchEvent = {
        clVM.isSwipe.value = false
    }
    //수정 항목 인덱스
    var openIndex = remember { mutableStateOf(-1) }
    //수정 트리거
    var openFlag = remember { mutableStateOf(false) }
    //ListTodo 리턴용
    var openInfo: Pair<MutableState<Boolean>, MutableState<Int>>

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
                    text = "",
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
                    //'clickable' callback 받음
                    openInfo = listTodo(clVM = clVM, openIndex = openIndex, openFlag = openFlag)
                    openFlag = openInfo.first
                    openIndex = openInfo.second
                }
            }
            //플로팅 버튼
            FloatingActions(context, clVM)
            //커스텀 스낵바
            CustomSnackBar(
                visible = clVM.isSwipe.value,
                text = "삭제되었습니다",
                //자동 종료
                launchedEffect = {
                    clVM.isSwipe.value = false
                }
            )
            //체크리스트 작성 보드
            openFlag = checkListWriteBoardWithBackGround(
                context = context,
                clVM = clVM,
                isPressed = openFlag,
                index = openIndex.value
            )
        }
    }
}

//투두 리스트 리사이클러뷰
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun listTodo(
    clVM: CheckListViewModel,
    openIndex: MutableState<Int> = mutableStateOf(-1),
    openFlag: MutableState<Boolean> = mutableStateOf(false)
): Pair<MutableState<Boolean>, MutableState<Int>> {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //리사이클러뷰
        items(
            count = clVM.checkList.size,
            key = { item: Int -> clVM.checkList[item].id },
        ) { index ->
            val currentItem by rememberUpdatedState(newValue = clVM.checkList[index])
            //체크리스트(스와이프) 정의
            ChecklistSwipable(
                modifier = Modifier
                    .animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing,
                        )
                    )//TODO basicMarquee
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        openIndex.value = index
                        openFlag.value = !openFlag.value
                    },
                item = clVM.checkList[index],
                index = index,
            ) {
                CoroutineScope(Dispatchers.Default).launch {
                    //너무 빨리 삭제되면 swipe 애니메이션이 제대로 출력 안됨
                    delay(300L)
                    //스와이프 시 삭제
                    clVM.checkList.remove(currentItem)
                    clVM.minCheckListId()
                    clVM.isSwipe.value = true
                }
            }
        }
    }
    return Pair(openFlag, openIndex)
}

//플로팅 버튼
@Composable
fun FloatingActions(context: Context, clVM: CheckListViewModel) {
    var isPressed = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 10.dp, end = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        FloatingActionButton(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            containerColor = Red2,
            onClick = {
                isPressed.value = !isPressed.value
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                tint = Color.White,
                contentDescription = "Add"
            )
        }
    }
    //플로팅 버튼 클릭 이벤트
    isPressed = checkListWriteBoardWithBackGround(context = context, clVM = clVM, isPressed = isPressed)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {
    WeeklyChecklistApp(MainActivity(), clVM = CheckListViewModel())
}
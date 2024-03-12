package com.weekly.weeklychecklist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.ui.CustomSnackBar
import com.weekly.weeklychecklist.ui.checkListWriteBoardWithBackGround
import com.weekly.weeklychecklist.ui.listTodo
import com.weekly.weeklychecklist.ui.theme.BoardBackground
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme
import com.weekly.weeklychecklist.util.CheckListUtils
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class MainActivity : ComponentActivity() {
    private val clVM: CheckListViewModel by viewModels()
    private val TAG = javaClass.simpleName
    var backPressedCount = 0
    var pressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DB init
        val db = CheckListDatabaseRepository.getInstance()
        db.initDatabase(this@MainActivity)

        onBackPressedDispatcher.addCallback(backPressedCallBack(this))

        setContent {
            WeeklyChecklistApp(context = this, clVM = clVM)
        }
        //splash screen
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener (
            object : ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    return if(clVM.isFinished){
                        //다 되면 화면 시작
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        Log.d(javaClass.simpleName, "Splash 화면 DB 통신 완료")
                        true
                    } else {
                        //splash 화면 중 데이터 베이스 init
                        Log.d(javaClass.simpleName, "Splash 화면 DB 통신 중")
                        clVM.getCheckLists()
                        clVM.isSplashed = true
                        runBlocking {
                            delay(100)
                        }
                        //DB GET -> 스위치 정렬이라 recomposition 2회 일어나는게 정상
                        clVM.switchInitialization(this@MainActivity)
                        false
                    }
                }
            }
        )
    }

    //뒤로가기 콜백
    private fun backPressedCallBack(context: Context) = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            backPressedCount++
            when(backPressedCount){
                1 -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(2000)
                        if(backPressedCount == 2)
                            return@launch
                        else
                            backPressedCount = 0
                    }
                    pressedTime = System.currentTimeMillis()
                    Toast.makeText(context, "한 번 더 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    if(System.currentTimeMillis() > pressedTime + 2000){
                        backPressedCount = 0
                    } else{
                        backPressedCount = 0
                        finishAndRemoveTask()
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        if(!clVM.isSplashed) {
            Log.d(javaClass.simpleName, "onResume 스위치 초기화")
            clVM.switchInitialization(this)
            clVM.onResumeRefreshed.value = !clVM.onResumeRefreshed.value
        }
    }

    override fun onPause(){
        super.onPause()
        Log.d(javaClass.simpleName, "onPause DB Insert")
        clVM.isSplashed = false
        clVM.updateCheckListAll()
    }
}

@Composable
fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event{
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{_, event ->
            state = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return state
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
    val util = CheckListUtils()
    //onResume 리컴포지션용
    var refreshing by remember { clVM.onResumeRefreshed }
//    val lifecycleEvent = rememberLifecycleEvent()

//    LaunchedEffect(lifecycleEvent){
//        when(lifecycleEvent){
//            Lifecycle.Event.ON_RESUME -> {
//                //요일 지나면 스위치 초기화
//                if(!clVM.isSplashed) {
//                    Log.d(javaClass.simpleName, "onResume 스위치 초기화")
//                    clVM.switchInitialization()
//                    refreshing = !refreshing
////                    clVM.customToggleRefreshingOnResumeState.value = refreshing
//                }
//            }
//            else -> {}
//        }
//    }

    key(refreshing) {
        val today = LocalDate.now()
        val week = util.convertDayOfWeekToMyDayOfWeek(today.dayOfWeek)
        var isPressed = remember { mutableStateOf(false) }

        WeeklyCheckListTheme {
            //뒷 배경
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = BoardBackground,
                        shape = RectangleShape
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //타이틀
                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = "${DateTimeFormatter.ofPattern("M월 dd일", Locale.KOREA).format(today)} ${week}요일",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        //추가 버튼
                        Icon(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    },
                                    indication = CheckListUtils.CustomIndication
                                ) {
                                    isPressed.value = !isPressed.value
                                },
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "추가",
                            tint = Color.White,
                        )
                        //test 요일 초기화 버튼
                        /*Text(
                            modifier = Modifier.padding(20.dp),
                            text = if(clVM.checkListUpdate.size != 0) "${DateTimeFormatter.ofPattern("M월 dd일", Locale.KOREA).format(clVM.checkListUpdate[0].registerTime)}" else "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Red
                        )
                        Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable(
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = CheckListUtils.CustomIndication
                                    ) {
                                        clVM.checkListUpdate[0].registerTime = LocalDateTime.now()
                                        clVM.updateCheckListUpdate(clVM.checkListUpdate[0])
                                    },
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "추가",
                        tint = Color.Red,
                        )*/
                    }
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
//                FloatingActions(context, clVM)
                //커스텀 스낵바
                CustomSnackBar(
                    visible = clVM.isSwipe.value,
                    text = "삭제되었습니다",
                    //자동 종료
                    launchedEffect = {
                        clVM.isSwipe.value = false
                    }
                )
                //항목 클릭 수정 화면
                openFlag = checkListWriteBoardWithBackGround(
                    clVM = clVM,
                    isPressed = openFlag,
                    index = openIndex.value
                )
                //add 버튼 추가 화면
                isPressed = checkListWriteBoardWithBackGround(clVM = clVM, isPressed = isPressed)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {
    WeeklyChecklistApp(MainActivity(), clVM = CheckListViewModel())
}

package com.weekly.weeklychecklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.ui.CustomSnackBar
import com.weekly.weeklychecklist.ui.FloatingActions
import com.weekly.weeklychecklist.ui.checkListWriteBoardWithBackGround
import com.weekly.weeklychecklist.ui.listTodo
import com.weekly.weeklychecklist.ui.observeAsState
import com.weekly.weeklychecklist.ui.theme.BoardBackground
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme
import com.weekly.weeklychecklist.util.LatestUIState
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    private val clVM: CheckListViewModel by viewModels()
    private val TAG = javaClass.simpleName
    var backPressedCount = 0
    var pressedTime = 0L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            //DB init
            val db = CheckListDatabaseRepository.getInstance()
            db.initDatabase(this@MainActivity)
        }

        setContent {
            WeeklyChecklistApp(context = this, clVM = clVM)
        }


        clVM.getCheckLists()

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                clVM.checkListE.collect{ uiState ->
//                    when(uiState){
//                        is LatestUIState.Success -> {
//                            clVM.checkList = uiState.checkList.toMutableStateList()
//                        }
//                        is LatestUIState.Error -> {
//                            Log.e(javaClass.simpleName,
//                                uiState.exception.toString()
//                            )
//                        }
//                    }
//                }
//            }
//        }


        onBackPressedDispatcher.addCallback(backPressedCallBack(this))
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
        //요일 지나면 스위치 초기화
        clVM.switchInitialization()
    }

    override fun onStop() {
        super.onStop()
//        clVM.updateIfDone()
        clVM.updateCheckListAll()
    }


    override fun onPause() {
        clVM.restartMainActivity = true
        super.onPause()
    }

    override fun onDestroy() {
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
    //TODO 화면만 재 랜더링 시키면 문제 없음
    val lifecycleEventFlag by LocalLifecycleOwner.current.lifecycle.observeAsState()

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
                clVM = clVM,
                isPressed = openFlag,
                index = openIndex.value
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {
    WeeklyChecklistApp(MainActivity(), clVM = CheckListViewModel())
}
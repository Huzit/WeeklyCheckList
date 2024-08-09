package com.weekly.weeklychecklist.ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.MainActivity
import com.weekly.weeklychecklist.R
import com.weekly.weeklychecklist.ui.theme.BoardBackground
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme
import com.weekly.weeklychecklist.util.CheckListUtils
import com.weekly.weeklychecklist.vm.CheckListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val refreshing by remember { clVM.onResumeRefreshed }

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
                                        if(clVM.checkListUpdate.isNotEmpty()) {
                                            clVM.checkListUpdate[0].registerTime = LocalDateTime.now()
                                            clVM.updateCheckListUpdate(clVM.checkListUpdate[0])
                                        }
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

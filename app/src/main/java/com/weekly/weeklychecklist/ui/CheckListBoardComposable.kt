package com.weekly.weeklychecklist.ui

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity
import com.weekly.weeklychecklist.ui.theme.AmbientGray
import com.weekly.weeklychecklist.ui.theme.ConfirmButton
import com.weekly.weeklychecklist.ui.theme.SpotColor
import com.weekly.weeklychecklist.vm.CheckListViewModel
import java.time.LocalDateTime

//체크리스트 작성 보드 + 백그라운드 + 애니메이션
@Composable
fun checkListWriteBoardWithBackGround(
    clVM: CheckListViewModel,
    index: Int = -1,
    isPressed: MutableState<Boolean>,
): MutableState<Boolean> {
    val mIsPressed = isPressed
    val halfHeight = LocalConfiguration.current.screenHeightDp / 2
    //작성보드 뒷 배경
    if (mIsPressed.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AmbientGray)
                .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                    mIsPressed.value = false
                },
        )
    }
    //팝업 애니메이션
    AnimatedVisibility(
        visible = mIsPressed.value,
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        enter = slideIn(initialOffset = { IntOffset(0, halfHeight) }) + fadeIn(initialAlpha = 0f),
        exit = slideOut(
            animationSpec = TweenSpec(100, 100, FastOutLinearInEasing),
            targetOffset = { IntOffset(0, halfHeight) }
        ) +
                fadeOut(
                    animationSpec = TweenSpec(100, 100, FastOutLinearInEasing),
                    targetAlpha = 0f
                )
    ) {
        //수정 시
        if (index > -1)
            ChecklistWriteBoard(clVM = clVM, index = index) {
                //확인 클릭 시
                mIsPressed.value = false
                //이전에 삭제 된 정보를 dismissState가 가지고 있음 -> 롤백 트리거 ON
                clVM.isSwipToDeleteCancel = true
            }
        //새로 생성
        else
            ChecklistWriteBoard(clVM = clVM) {
                mIsPressed.value = false
                clVM.isSwipToDeleteCancel = true
            }
    }
    return mIsPressed
}

//체크리스트 입력 보드
@Composable
fun ChecklistWriteBoard(
    clVM: CheckListViewModel,
    index: Int = -1,
    height: Dp = 350.dp,
    fontSize: TextUnit = 24.sp,
    buttonOnClick: () -> Unit,
) {
    val checkList by clVM.checkList.collectAsState()
    var checklistContent = if (index == -1) "" else checkList[index].checklistContent
    var myDayOfWeek =
        remember { if (index == -1) mutableSetOf(MyDayOfWeek.널) else checkList[index].restartWeek.toMutableSet() }
    var buttonFlag by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        color = Color.White,
        elevation = 10.dp,
        shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            //핸들
            Box(
                modifier = Modifier
                    .width(145.dp)
                    .height(5.dp)
                    .background(
                        color = SpotColor,
                        shape = RoundedCornerShape(percent = 100),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "할 일",
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    CustomSpacer(height = 20.dp)

                    //할 일 입력
                    checklistContent = customTextField(textEntered = checklistContent)

                    CustomSpacer(height = 20.dp)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "초기화 요일",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CustomSpacer(height = 20.dp)

                    //요일 버튼
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (week in MyDayOfWeek.values().filter { it != MyDayOfWeek.널 }) {
                            //클릭 후 취소 및 수정 시 선택된 요일 반영
                            if (weekSelectButton(
                                    week,
                                    isContain = myDayOfWeek.contains(week)
                                ) == MyDayOfWeek.널
                            )
                                myDayOfWeek.remove(week)
                            else
                                myDayOfWeek.add(week)
                        }
                    }
                    myDayOfWeek = myDayOfWeek.filter { it != MyDayOfWeek.널 }.toMutableSet()

                    CustomSpacer(height = 20.dp)
                    //확인 버튼
                    Button(
                        modifier = Modifier
                            .width(210.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(ConfirmButton),
                        onClick = {
                            Log.d("CheckListWriteBoard", "current selected index : $index")
                            //요일, 내용 검증
                            if (checklistContent.isNotEmpty() && myDayOfWeek.isNotEmpty()) {
                                buttonFlag = false
                                //새로작성
                                if (index == -1) {
                                    //DB
                                    clVM.insertCheckList(
                                        listName = "default",
                                        checkListContent = checklistContent,
                                        restartWeek = myDayOfWeek,
                                        done = false,
                                        lastUpdatedDate = LocalDateTime.now()
                                    )
                                    clVM.insertCheckListUpdate(
                                        listName = "default",
                                        isUpdated = false,
                                        registerTime = LocalDateTime.now()
                                    )
                                    //UI
                                    checkList.add(
                                        CheckListEntity(
                                            listName = "default",
                                            checklistContent = checklistContent,
                                            restartWeek = myDayOfWeek,
                                            registerTime = LocalDateTime.now()
                                        )
                                    )
                                    clVM.checkListUpdate.add(
                                        CheckListUpdateEntity(
                                            listName = "default",
                                            isUpdate = false,
                                            registerTime = LocalDateTime.now()
                                        )
                                    )
                                }
                                //수정
                                else {
                                    Log.d("CheckListWriteBoard", "checkList update is start")

                                    checkList[index].checklistContent = checklistContent
                                    checkList[index].restartWeek = myDayOfWeek

                                    clVM.updateCheckListUpdate(
                                        clVM.checkListUpdate[0].apply {
                                            isUpdate = true
                                            registerTime = LocalDateTime.now()
                                        }
                                    )
                                    clVM.updateCheckList(
                                        idx = checkList[index].idx,
                                        listName = "default",
                                        checkListContent = checklistContent,
                                        restartWeek = myDayOfWeek,
                                        done = false,
                                        lastUpdatedDate = LocalDateTime.now()
                                    )
                                }
                                buttonOnClick()
                            } else
                                buttonFlag = true
                        }
                    ) {
                        if (buttonFlag) {
                            if (checklistContent.isEmpty())
                                Toast.makeText(
                                    LocalContext.current,
                                    "할일이 비었습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else if (myDayOfWeek.isEmpty())
                                Toast.makeText(
                                    LocalContext.current,
                                    "요일이 비었습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }

                        Text(
                            text = "확인",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
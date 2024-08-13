package com.weekly.weeklychecklist.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weekly.weeklychecklist.R
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.ui.theme.BorderColor
import com.weekly.weeklychecklist.ui.theme.CheckListBackground
import com.weekly.weeklychecklist.ui.theme.Green
import com.weekly.weeklychecklist.ui.theme.Red
import com.weekly.weeklychecklist.ui.theme.SuperLightGray
import com.weekly.weeklychecklist.ui.theme.SwipeBackground
import com.weekly.weeklychecklist.vm.CheckListViewModel

//스와이프 삭제기능
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChecklistSwipable(
    modifier: Modifier,
    itemIndex: Int,
    entity: CheckListEntity,
    clViewModel: CheckListViewModel,
    dismissToDelete: () -> Unit,
) {
    val dismissState = rememberDismissState(
        initialValue = DismissValue.Default,
        confirmStateChange = {
            //Start로 dismiss시
            if (it == DismissValue.DismissedToStart) {
                //삭제이벤트
                dismissToDelete()
                clViewModel.isSwipToDeleteCancel = false
                true
            } else {
                false
            }
        }
    )
    //삭제 작업 완료됬을 때 롤백
    if (clViewModel.isSwipToDeleteCancel)
        if (dismissState.currentValue != DismissValue.Default) {
            LaunchedEffect(Unit) {
                dismissState.reset()
            }
        }

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        dismissThresholds = { FractionalThreshold(0.6f) },
        //스와이프 방향(기본값 양측)
        directions = setOf(DismissDirection.EndToStart),
        //swipe 되기 전 보여줄 화면
        dismissContent = {
            CheckListBox(entity, itemIndex, clViewModel)
        },
        background = {
            val color by animateColorAsState(SwipeBackground.copy(), label = "")
            val icon = painterResource(id = R.drawable.delete)
            val scale by animateFloatAsState(1.0f, label = "")

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = RoundedCornerShape(percent = 20))
                    .padding(horizontal = 30.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    modifier = Modifier
                        .scale(scale)
                        .size(24.dp),
                    painter = icon,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
    )
}

//체크리스트 항목
@Composable
fun CheckListBox(
    entity: CheckListEntity,
    itemIndex: Int,
    clViewModel: CheckListViewModel
) {
    val configuration = LocalConfiguration.current
    val backgroundWidth: Dp = configuration.screenWidthDp.minus(20).dp
    val backgroundHeight: Dp = 72.dp
    val width = configuration.screenWidthDp.minus(160).dp
    val height = 60.dp
    val cornerSize = 20
    Surface(
        modifier = Modifier
            .size(width = backgroundWidth, height = backgroundHeight),
        color = CheckListBackground,
        shape = RoundedCornerShape(cornerSize),
        border = BorderStroke(1.dp, color = SuperLightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(width = width, height = height),
                shape = RoundedCornerShape(cornerSize),
                colors = CardDefaults.cardColors(SuperLightGray)
            ) {
                Box(
                    modifier = Modifier
                        .size(
                            width = width,
                            height = height
                        )
                        .background(
                            color = SuperLightGray,
                            shape = RoundedCornerShape(percent = cornerSize),
                        )
                        .border(
                            shape = RoundedCornerShape(percent = cornerSize),
                            width = 1.dp,
                            color = BorderColor
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = entity.checklistContent,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth(),
                        maxLines = 3,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Clip,
                        lineHeight = 14.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            //무작위로 들어오는 요일을 월 ~ 금으로 정렬
            val weeks = entity.restartWeek.toString().filter { it != ' ' && it != '[' && it != ']' }
                .split(",")
            val result = StringBuilder()
            var resultWeeks = ""
            //sort
            val myDayOfWeek = listOf<String>("월", "화", "수", "목", "금", "토", "일")

            for (day in myDayOfWeek) {
                if (weeks.contains(day))
                    result.append("$day ")
            }
            if (result.isNotEmpty())
                resultWeeks = result.deleteCharAt(result.lastIndex).toString()
            if (resultWeeks == "월 화 수 목 금 토 일")
                resultWeeks = "매일"

            Column() {
                Text(
                    text = resultWeeks,
                    fontSize = dpToSp(dp = 10.dp),
                    modifier = Modifier.padding(start = 7.dp)
                )
                CustomToggleButton(itemIndex = itemIndex, entity = entity, clViewModel = clViewModel)
            }
        }
    }
}

//커스텀 스위치
@Composable
fun CustomToggleButton(
    entity: CheckListEntity,
    itemIndex: Int,
    clViewModel: CheckListViewModel
) {
    val width: Dp = 95.dp
    val height: Dp = 45.dp
    val trackColor: Color = BorderColor
    val gapBetweenThumbAndTrackEdge: Dp = 5.dp
    val borderWidth: Dp = 1.dp
    val cornerSize = 50
    val iConInnerPadding: Dp = 4.dp
    val switchSize: Dp = 40.dp

    val checkList by clViewModel.checkList.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    var switchOn by remember { mutableStateOf(checkList[itemIndex].done) }
    val alignment by animateAlignmentAsState(if (switchOn) 1f else -1f)

//    key(clViewModel.customToggleRefreshingDraggableState) {
//        switchOn = entity.done
//    }
    //테두리 Border
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .background( //뒷 배경
                color = SuperLightGray,
                shape = RoundedCornerShape(percent = cornerSize)
            )
            .border( //테두리
                width = borderWidth,
                color = trackColor,
                shape = RoundedCornerShape(percent = cornerSize)
            )
            .clickable(
                //클릭 설정
                indication = null,
                interactionSource = interactionSource,
            ) {
                switchOn = !switchOn
                checkList[itemIndex].done = switchOn
            },
        contentAlignment = Alignment.Center,
    ) {
        //백그라운드 아이콘
        Row {
            Icon(
                painter = painterResource(id = R.drawable.done),
                contentDescription = "DONE",
                tint = Green,
            )
            Icon(
                painter = painterResource(id = R.drawable.not_yet),
                contentDescription = "NOT YET",
                tint = Red,
            )
        }
        //내부 버튼을 위한 패딩
        Box(
            modifier = Modifier
                .padding(
                    start = gapBetweenThumbAndTrackEdge,
                    end = gapBetweenThumbAndTrackEdge
                )
                .fillMaxSize(),
            contentAlignment = alignment
        ) {
            //스위치 아이콘
            Icon(
                painter = painterResource(id = R.drawable.switch_circle),
                contentDescription = if (switchOn) "Enabled" else "Disabled",
                modifier = Modifier
                    .size(switchSize)
                    .background(
                        color = Color.White,
                        shape = CircleShape,
                    )
                    .border(1.dp, Color.Gray, CircleShape)
                    .padding(all = iConInnerPadding),
            )
        }
    }
}
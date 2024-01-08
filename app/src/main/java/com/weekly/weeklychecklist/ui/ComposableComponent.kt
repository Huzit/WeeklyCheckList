package com.weekly.weeklychecklist.ui

import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.R
import com.weekly.weeklychecklist.database.CheckListDatabaseRepository
import com.weekly.weeklychecklist.database.entity.CheckListEntity
import com.weekly.weeklychecklist.database.entity.CheckListUpdateEntity
import com.weekly.weeklychecklist.ui.theme.AmbientGray
import com.weekly.weeklychecklist.ui.theme.BorderColor
import com.weekly.weeklychecklist.ui.theme.CheckListBackground
import com.weekly.weeklychecklist.ui.theme.ClickedYellow
import com.weekly.weeklychecklist.ui.theme.ConfirmButton
import com.weekly.weeklychecklist.ui.theme.Green
import com.weekly.weeklychecklist.ui.theme.Red
import com.weekly.weeklychecklist.ui.theme.SpotColor
import com.weekly.weeklychecklist.ui.theme.SuperLightGray
import com.weekly.weeklychecklist.ui.theme.SwipeBackground
import com.weekly.weeklychecklist.vm.CheckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event>{
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this){
        val observer = LifecycleEventObserver{ _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}

//투두 리스트 리사이클러뷰
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun listTodo(
    clVM: CheckListViewModel,
    openIndex: MutableState<Int> = mutableStateOf(-1),
    openFlag: MutableState<Boolean> = mutableStateOf(false)
): Pair<MutableState<Boolean>, MutableState<Int>> {
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropStste(listState){ fromIndex, toIndex ->
        clVM.checkList = clVM.checkList.apply {
            val text = this[fromIndex]
            this[fromIndex] = this[toIndex]
            this[toIndex] = text
        }
    }
    var dialogVisible by remember {mutableStateOf(false)}
    var currentIndex by remember {mutableStateOf(-1)}


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .dragContainer(dragDropState),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //리사이클러뷰
        items(
            count = clVM.checkList.size
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
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing,
                            )
                        ),
                    item = clVM.checkList[index],
                    itemIndex = index
                ) {
                    currentIndex = index
                    dialogVisible = true
                }
            }
        }
    }
    if(dialogVisible) {
        CustomAlertDialog(
            message = "삭제하시겠습니까?",
            positiveEvent = {
                CoroutineScope(Dispatchers.Default).launch {
                    //너무 빨리 삭제되면 swipe 애니메이션이 제대로 출력 안됨
                    val current = clVM.checkList[currentIndex]
                    delay(500L)
                    clVM.checkList.remove(current)
                    clVM.deleteCheckList(current.idx)
                }
                //롤백 트리거
                clVM.isSwipToDeleteCancel = true
                dialogVisible = false
            },
            negativeEvent = {
                clVM.isSwipToDeleteCancel = true
                dialogVisible = false
            })
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
            containerColor = ConfirmButton,
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
    isPressed = checkListWriteBoardWithBackGround(clVM = clVM, isPressed = isPressed)
}

//체크리스트 항목
@Composable
fun CheckListBox(
    item: CheckListEntity,
    itemIndex: Int
) {
    val configuration = LocalConfiguration.current
    val backgroundWidth: Dp = configuration.screenWidthDp.minus(20).dp
    val backgroundHeight: Dp = 72.dp
    val width = configuration.screenWidthDp.minus(160).dp
    val height = 60.dp
    val cornerSize = 20
    val done = remember { mutableStateOf(item.done) }

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
                        text = item.checklistContent,
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
            val weeks = item.restartWeek.toString().filter { it != ' ' && it != '[' && it != ']' }
                .split(",")
            val result = StringBuilder()
            var resultWeeks = ""
            //sort
            val myDayOfWeek = listOf<String>("월", "화", "수", "목", "금", "토", "일")

            for(day in myDayOfWeek){
                if(weeks.contains(day))
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
                CustomToggleButton(isCheck = done.value, itemIndex = itemIndex)
            }
        }
    }
}

//스와이프 삭제기능
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChecklistSwipable(
    modifier: Modifier,
    item: CheckListEntity,
    itemIndex: Int,
    dismissToDelete: () -> Unit,
) {
    val clVM = viewModel<CheckListViewModel>()
    val dismissState = rememberDismissState(
        initialValue = DismissValue.Default,
        confirmStateChange = {
            //Start로 dismiss시
            if (it == DismissValue.DismissedToStart) {
                //삭제이벤트
                dismissToDelete()
                clVM.isSwipToDeleteCancel = false
                true
            } else{
                false
            }
        }
    )
    //삭제 작업 완료됬을 때 롤백
    if(clVM.isSwipToDeleteCancel)
        if(dismissState.currentValue != DismissValue.Default){
            LaunchedEffect(Unit){
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
            CheckListBox(item, itemIndex)
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

//커스텀 스위치
@Composable
fun CustomToggleButton(
    isCheck: Boolean,
    itemIndex: Int
) {
    val width: Dp = 95.dp
    val height: Dp = 45.dp
    val trackColor: Color = BorderColor
    val gapBetweenThumbAndTrackEdge: Dp = 5.dp
    val borderWidth: Dp = 1.dp
    val cornerSize = 50
    val iConInnerPadding: Dp = 4.dp
    val switchSize: Dp = 40.dp

    val clVM = viewModel<CheckListViewModel>()
    val interactionSource = remember { MutableInteractionSource() }
    var switchOn by remember { mutableStateOf(isCheck) }
    val alignment by animateAlignmentAsState(if (switchOn) 1f else -1f)

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
                clVM.checkList[itemIndex].done = switchOn
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

//할 일 텍스트 필드
@Composable
fun customTextField(
    fontSize: TextUnit = 20.sp,
    height: Dp = 75.dp,
    padding: Dp = 5.dp,
    shapePercent: Int = 10,
    textEntered: String,
): String {
    var text by remember { mutableStateOf(textEntered) }
    BasicTextField(
        value = text,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(SuperLightGray, shape = RoundedCornerShape(percent = shapePercent))
            .padding(padding),
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            textAlign = TextAlign.Start
        ),
        maxLines = 3,
        onValueChange = { newText ->
            text = newText
        }
    )
    return text
}

//요일 선택 버튼
@Composable
fun weekSelectButton(
    week: MyDayOfWeek,
    size: Dp = 35.dp,
    fontSize: TextUnit = 15.sp,
    isClicked: Boolean = false,
    isContain: Boolean = false,
): MyDayOfWeek {
    //클릭 여부
    var isClicked by remember { mutableStateOf(isClicked) }
    //수정 시 포함 여부
    var isContain by remember { mutableStateOf(isContain) }
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }
    //중복 자료 방지를 위해 Set 사용
    val returnSet = remember { mutableSetOf<MyDayOfWeek>() }
    //수정 시 DB의 요일에 포함
    if(isContain){
        returnSet.add(week)
        backgroundColor = ClickedYellow
        isContain = false
        isClicked = true
    }
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .size(size)
                .background(color = Color.Transparent, shape = CircleShape),
            colors = ButtonDefaults.buttonColors(backgroundColor),
            border = BorderStroke(2.dp, Color.Black),
            onClick = {
                isClicked = !isClicked
                backgroundColor = if(isClicked){
                    //클릭 시 추가
                    returnSet.add(week)
                    ClickedYellow
                } else{
                    //한번 더 클릭하면 삭제
                    returnSet.remove(week)
                    Color.Transparent
                }
            }
        ) {}
        //일부러 이 위치, 브라켓 안에 넣으면 텍스트 미출력
        Text(
            text = week.name,
            fontSize = fontSize,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
    return if (returnSet.isNotEmpty()) returnSet.first() else MyDayOfWeek.널
}

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
    if(mIsPressed.value) {
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
        if(index > -1)
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
    var checklistContent = if(index == -1) "" else clVM.checkList[index].checklistContent
    var myDayOfWeek = remember { if(index == -1) mutableSetOf(MyDayOfWeek.널) else clVM.checkList[index].restartWeek.toMutableSet() }
    val checkListRepository = CheckListDatabaseRepository()
    var buttonFlag by remember { mutableStateOf(false) }
    val db = CheckListDatabaseRepository.getInstance()

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
                            if (weekSelectButton(week, isContain = myDayOfWeek.contains(week)) == MyDayOfWeek.널)
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
                            if(checklistContent.isNotEmpty() && myDayOfWeek.isNotEmpty()){
                                buttonFlag = false
                                //새로작성
                                if(index == -1) {
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
                                    clVM.checkList.add(
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

                                    clVM.checkList[index].checklistContent = checklistContent
                                    clVM.checkList[index].restartWeek = myDayOfWeek

                                    clVM.updateCheckListUpdate(
                                        clVM.checkListUpdate[0].apply {
                                            isUpdate = true
                                            registerTime = LocalDateTime.now()
                                        }
                                    )
                                    clVM.updateCheckList(
                                        idx = clVM.checkList[index].idx,
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
                        if(buttonFlag) {
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

@Composable
fun CustomSpacer(height: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    )
}
//다이얼로그
@Composable
fun CustomAlertDialog(
    message: String,
    positiveEvent: () -> Unit,
    negativeEvent: () -> Unit,
) {
    val width: Dp = 300.dp
    val height: Dp = 200.dp
    val fontSize: TextUnit = 23.sp
    val buttonWidth: Dp = 120.dp
    val buttonHeight: Dp = 50.dp
    val buttonFontSize: TextUnit = 18.sp

    AlertDialog(
        modifier = Modifier.size(width, height),
        onDismissRequest = {},
        title = {},
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = message,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        buttons = {
            CustomSpacer(height = 20.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(buttonHeight),
                    colors = ButtonDefaults.buttonColors(ConfirmButton),
                    onClick = negativeEvent
                ) {
                    Text(
                        text = "취소",
                        fontWeight = FontWeight.Bold,
                        fontSize = buttonFontSize
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(buttonHeight),
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    onClick = positiveEvent
                ) {
                    Text(
                        text = "확인",
                        fontWeight = FontWeight.Bold,
                        fontSize = buttonFontSize
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
//Custom SnackBar
//visible과 LaunchedEffect로 직접 종료 트리거 설정해줘야함
@Composable
fun CustomSnackBar(visible: Boolean, text: String, launchedEffect: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = TweenSpec(200, 100, FastOutLinearInEasing)),
        exit = fadeOut(animationSpec = TweenSpec(200, 100, FastOutLinearInEasing))
    ) {
        Surface(
            modifier = Modifier,
            color = Color.Transparent
        ) {
            Snackbar(
                modifier = Modifier
                    .width(LocalConfiguration.current.screenWidthDp.minus(20).dp),
                shape = RoundedCornerShape(percent = 30),
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = text, color = Color.White)
                }
                LaunchedEffect(Unit) {
                    delay(3500L)
                    launchedEffect()
                }
            }
        }
    }
}

//스위치 애니메이션 컴포저블
@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetValue = targetBiasValue)
    return remember { derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) } }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

@Preview(showBackground = false)
@Composable
fun SwitchPreview() {
    val context = LocalContext.current
    Column {
        weekSelectButton(MyDayOfWeek.널)
        CustomToggleButton(isCheck = true, 0)
        CustomToggleButton(isCheck = false, 0)
        ChecklistWriteBoard(clVM = CheckListViewModel()) {}
        CustomSnackBar(visible = true, text = "TestText") {
        }
//        CheckListBox(
//            item = CheckListInfo(1, "preview content", setOf(MyDayOfWeek.월), false),
//            1,
//        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun testPrevicew() {
    CustomAlertDialog(message = "초기화 ㄱ?", positiveEvent = { /*TODO*/ }, negativeEvent = {})
}

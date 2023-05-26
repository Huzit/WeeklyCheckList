package com.weekly.weeklychecklist.ui

import android.hardware.lights.Light
import android.util.Log
import android.widget.Space
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextButton
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.R
import com.weekly.weeklychecklist.ui.theme.BorderColor
import com.weekly.weeklychecklist.ui.theme.ClickedYellow
import com.weekly.weeklychecklist.ui.theme.DialogShadow
import com.weekly.weeklychecklist.ui.theme.Green
import com.weekly.weeklychecklist.ui.theme.Red
import com.weekly.weeklychecklist.ui.theme.Red1
import com.weekly.weeklychecklist.ui.theme.Red2
import com.weekly.weeklychecklist.ui.theme.SpotColor
import com.weekly.weeklychecklist.ui.theme.SuperLightGray

class ComposableComponent {
}


//체크리스트 항목
@Composable
fun ChecklistBox(
    text: String
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
        color = Color.White,
        shape = RoundedCornerShape(cornerSize),
        border = BorderStroke(1.dp, color = SuperLightGray)
//        elevation = 10.dp,
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
                            //뒷 배경
                            color = SuperLightGray,
                            shape = RoundedCornerShape(percent = cornerSize),
                        )
                        .border(
                            shape = RoundedCornerShape(percent = cornerSize),
                            width = 3.dp,
                            color = BorderColor
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = text,
                        fontSize = 18.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Clip
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            CustomToggleButton(isCheck = false)
        }
    }
}

//스와이프 삭제기능
@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun ChecklistSwipable() {
    val swipeState = rememberDismissState(confirmStateChange = {
        //dismiss 됐을 때 행동
        if (it == DismissValue.DismissedToStart) {
            Log.d("이거", "삭제됨")
        }
        true
    })
    
    SwipeToDismiss(
        state = swipeState,
        dismissThresholds = { FractionalThreshold(0.1f) },
        //스와이프 방향(기본값 양측)
        directions = setOf(DismissDirection.EndToStart),
        //swipe 되기 전 보여줄 화면
        dismissContent = {
            ChecklistBox(text = "TestText")
        },
        background = {
            val direction = swipeState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (swipeState.targetValue) {
                    DismissValue.Default -> SnackbarDefaults.backgroundColor.copy(alpha = 0.5f)
                    DismissValue.DismissedToStart -> Red1.copy()
                    DismissValue.DismissedToEnd -> SnackbarDefaults.backgroundColor.copy(alpha = 0.5f)
                }
            )
            val icon = when (swipeState.targetValue) {
                DismissValue.Default -> painterResource(id = R.drawable.switch_circle)
                DismissValue.DismissedToStart -> painterResource(id = R.drawable.delete)
                DismissValue.DismissedToEnd -> painterResource(id = R.drawable.delete)
            }
            val scale by animateFloatAsState(
                when (swipeState.targetValue == DismissValue.Default) {
                    true -> 0.5f
                    else -> 1.0f
                }
            )
            val alignment = Alignment.CenterEnd
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = RoundedCornerShape(percent = 20))
                    .padding(horizontal = 30.dp),
                contentAlignment = alignment
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

//그림자 테스트용
@Composable
fun InnerShadow(width: Dp, height: Dp, content: @Composable BoxScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(SuperLightGray)
    ) {
        //위쪽 그림자
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SpotColor,
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 23f,
                    ),
                ),
        ) {
            //좌측 그림자
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                SpotColor,
                                Color.Transparent,
                            ),
                            startX = 0f,
                            endX = 15f
                        )
                    )
            )
            //좌대각 그림자
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SpotColor,
                                Color.Transparent
                            ),
                            end = Offset(23f, 23f)
                        )
                    )
            )
            //우대각 그림자
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SpotColor,
                                Color.Transparent,
                            ),
                            start = Offset(x = Float.POSITIVE_INFINITY, y = 0f),
                            end = Offset(000f, 1000f)
                        )
                    ), contentAlignment = Alignment.CenterEnd
            ) {
                //우측 그림자
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    SpotColor
                                ),
                                startX = 0f,
                                endX = 15f
                            )
                        ),
                )
                content()
            }
        }
    }
}

//커스텀 스위치
@Composable
fun CustomToggleButton(
    width: Dp = 95.dp,
    height: Dp = 45.dp,
    trackColor: Color = BorderColor,
    gapBetweenThumbAndTrackEdge: Dp = 5.dp,
    borderWidth: Dp = 3.dp,
    cornerSize: Int = 50,
    iConInnerPadding: Dp = 4.dp,
    switchSize: Dp = 40.dp,
    isCheck: Boolean,
) {
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
                        shape = CircleShape
                    )
                    .padding(all = iConInnerPadding),
            )
        }
    }
}

//할 일 텍스트 필드
@Composable
fun CustomTextField(
    fontSize: TextUnit = 20.sp,
    height: Dp = 75.dp,
    padding: Dp = 5.dp,
    shapePercent: Int = 10
){
    var text by remember { mutableStateOf(TextFieldValue("")) }
    BasicTextField(
        value = text,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(SuperLightGray, shape = RoundedCornerShape(percent = shapePercent))
            .padding(padding)
        ,
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            textAlign = TextAlign.Start
        ),
        maxLines = 3
        ,onValueChange = {newText ->
            text = newText
        }
    )
}

//요일 선택 버튼
@Composable
fun WeekSelectButton(
    week: String,
    size: Dp = 35.dp,
    fontSize: TextUnit = 15.sp
){
    val weekText by remember{ mutableStateOf(week) }
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }
    var isClicked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ){
        Button(
            modifier = Modifier
                .size(size)
                .background(color = Color.Transparent, shape = CircleShape),
            colors = ButtonDefaults.buttonColors(backgroundColor),
            border = BorderStroke(2.dp, Color.Black),
            onClick = {
                isClicked = !isClicked
                backgroundColor = if(isClicked){
                    ClickedYellow
                } else{
                    Color.Transparent
                }
            }
        ){}
        //일부러 이 위치, 브라켓 안에 넣으면 텍스트 미출력
        Text(
            text = weekText,
            fontSize = fontSize,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
//요일 선택버튼 레이아
@Composable
fun WeekSelectButtonList(
    space: Dp = 10.dp
){
    Row{
        WeekSelectButton("월")
        Spacer(modifier = Modifier.size(space))
        WeekSelectButton("화")
        Spacer(modifier = Modifier.size(space))
        WeekSelectButton("수")
        Spacer(modifier = Modifier.size(space))
        WeekSelectButton("목")
        Spacer(modifier = Modifier.size(space))
        WeekSelectButton("금")
        Spacer(modifier = Modifier.size(space))
        WeekSelectButton("토")
        Spacer(modifier = Modifier.size(space))
        WeekSelectButton("일")
    }
}

//체크리스트 입력 보드
@Composable
fun ChecklistWriteBoard(
    height: Dp = 350.dp,
    fontSize: TextUnit = 24.sp
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        color = Color.White,
        elevation = 10.dp,
        shape = RoundedCornerShape(percent = 10)
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
                    CustomTextField()
                    CustomSpacer(height = 20.dp)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "초기화 요일",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CustomSpacer(height = 20.dp)
                    WeekSelectButtonList()
                    CustomSpacer(height = 20.dp)
                    //확인 버튼
                    Button(
                        modifier = Modifier
                            .width(210.dp)
                            .height(50.dp)
                        ,
                        colors = ButtonDefaults.buttonColors(Red2),
                        onClick = { /*TODO*/ }
                    ) {
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
fun CustomSpacer(height: Dp){
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
fun CustomAlertDialog(
    message: String,
    positiveEvent: () -> Unit,
    negativeEvent: () -> Unit,
){
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
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(buttonHeight),
                    colors = ButtonDefaults.buttonColors(Red2),
                    onClick = positiveEvent
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
                    onClick = negativeEvent
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

//플로팅 액션버튼
@Composable
fun AddCheckListButton(onClick: () -> Unit){
    FloatingActionButton(
        modifier = Modifier.size(70.dp),
        backgroundColor = Red2,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add) ,
            tint = Color.White,
            contentDescription = "Add"
        )
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

@Preview(showBackground = false)
@Composable
fun SwitchPreview() {
    Column {
        WeekSelectButton(week = "월")
        AddCheckListButton(){}
        CustomToggleButton(isCheck = true)
        CustomToggleButton(isCheck = false)
        ChecklistSwipable()
        ChecklistWriteBoard()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun testPrevicew(){
    CustomAlertDialog(message = "초기화 ㄱ?", positiveEvent = { /*TODO*/ }, negativeEvent = {})
}
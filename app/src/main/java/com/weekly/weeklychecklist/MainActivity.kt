package com.weekly.weeklychecklist

import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.text.Layout
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.ui.theme.*
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyChecklistApp(this)
        }
    }
}

@Composable
fun TextBox(
    width: Dp = 230.dp,
    height: Dp = 60.dp,
    cornerSize: Int = 20,
    text: String
) {
    Card(
        modifier = Modifier
            .size(width = width, height = height),
        shape = RoundedCornerShape(cornerSize),
        colors = CardDefaults.cardColors(SwitchBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = width,
                    height = height
                )
                .background(
                    //뒷 배경
                    color = SwitchBackgroundColor,
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
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun SwipeableSample() {
    val swipeState = rememberDismissState(confirmStateChange = {
        //dismiss됐을 때 행동
        if (it == DismissValue.DismissedToStart) {
            //삭제
        }
        true
    })

    SwipeToDismiss(
        state = swipeState,
        dismissThresholds = { FractionalThreshold(0.25f) },
        //swipe 되기 전 보여줄 화면
        dismissContent = {
            TextBox(text = "TestText")
        },
        background = {
            val direction = swipeState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when(swipeState.targetValue){
                    DismissValue.Default            -> backgroundColor.copy(alpha = 0.5f)
                    DismissValue.DismissedToStart   -> Color.Red.copy(alpha = 0.5f)
                    DismissValue.DismissedToEnd     -> Color.Green.copy(alpha = 0.5f)
                }
            )
            val icon = when(swipeState.targetValue){
                DismissValue.Default            -> painterResource(id = R.drawable.switch_circle)
                DismissValue.DismissedToStart   -> painterResource(id = R.drawable.done)
                DismissValue.DismissedToEnd     -> painterResource(id = R.drawable.not_yet)
            }
            val scale by animateFloatAsState(
                when(swipeState.targetValue == DismissValue.Default){
                    true -> 0.5f
                    else -> 1.5f
                }
            )
            val alignment = when (direction){
                DismissDirection.EndToStart -> Alignment.CenterEnd
                DismissDirection.StartToEnd -> Alignment.CenterStart
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 30.dp),
                contentAlignment = alignment
            ){
                Icon(
                    modifier = Modifier.scale(scale),
                    painter = icon,
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
        colors = CardDefaults.cardColors(SwitchBackgroundColor)
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
                color = SwitchBackgroundColor,
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

//스위치 애니메이션 컴포저블
@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetValue = targetBiasValue)
    return remember { derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) } }
}


//앱 전체 컴포저블
@Composable
fun WeeklyChecklistApp(main: MainActivity) {
    WeeklyCheckListTheme {
        Surface(
            modifier = Modifier,
        ) {
            Column {
                Row {
                    TextBox(text = "가가가가가가가가가가가가가가가가가가가가가가가가가가가가")
                    CustomToggleButton(isCheck = false)
                }
                SwipeableSample()
            }
        }
    }
}


@Preview(showBackground = false)
@Composable
fun SwitchPreview() {
    Column {
        TextBox(text = "TestText")
        CustomToggleButton(isCheck = true)
        CustomToggleButton(isCheck = false)
        SwipeableSample()
    }
}
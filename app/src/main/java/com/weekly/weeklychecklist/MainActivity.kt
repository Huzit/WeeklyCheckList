package com.weekly.weeklychecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.ui.theme.*

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
    Box(
        modifier = Modifier
            .size(
                width = width,
                height = height
            )
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(percent = cornerSize),
                ambientColor = AmbientGray,
                spotColor = SpotColor
            )
            .background( //뒷 배경
                color = SwitchBackgroundColor,
                shape = RoundedCornerShape(percent = cornerSize)
            )
            .border(
                shape = RoundedCornerShape(percent = cornerSize),
                width = 3.dp,
                color = BorderColor
            ),
        contentAlignment = Alignment.CenterStart
    ){
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
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(percent = cornerSize),
                ambientColor = AmbientGray,
                spotColor = SpotColor
            )
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
            Row {
                TextBox(text =
                "가가가가가가가가가가가가가가가가가가가가가가가가가가가가")
                CustomToggleButton(isCheck = false)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
}

@Preview(showBackground = false)
@Composable
fun SwitchPreview() {
    Column {
        TextBox(text = "TestText")
        CustomToggleButton(isCheck = true)
        CustomToggleButton(isCheck = false)
    }
}
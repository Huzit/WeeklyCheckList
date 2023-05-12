package com.weekly.weeklychecklist

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyChecklistApp(this)
        }
    }
}

//https://semicolonspace.com/jetpack-compose-custom-switch-button-icon/
//커스텀 스위치
@Composable
fun CustomToggleButton(
    modifier: Modifier = Modifier,
    width: Dp = 93.dp,
    height: Dp = 43.dp,
    trackColor: Color = Color(0x274D4D4D),
    gapBetweenThumbAndTrackEdge: Dp = 8.dp,
    borderWidth: Dp = 3.dp,
    cornerSize: Int = 50,
    iConInnerPadding: Dp = 4.dp,
    thumbSize: Dp = 35.dp,
    isCheck: Boolean,
){
    val interactionSource = remember { MutableInteractionSource() }
    var switchOn by remember{ mutableStateOf(isCheck) }
    val alignment by animateAlignmentAsState(if (switchOn) 1f else -1f)

    //테두리 Border
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(percent = cornerSize),
                ambientColor = colorResource(id = R.color.ambientGray),
                spotColor = colorResource(id = R.color.spotColor)
            )
            .size(width = width, height = height)
            .background( //뒷 배경
                color = colorResource(id = R.color.switch_background_color),
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
    ){
        //백그라운드 아이콘
        Row {
            Icon(
                painter = painterResource(id = R.drawable.done),
                contentDescription = "DONE",
                tint = colorResource(id = R.color.green),
            )
            Icon(
                painter = painterResource(id = R.drawable.not_yet),
                contentDescription = "NOT YET",
                tint = colorResource(id = R.color.red),
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
        ){
            //스위치 아이콘
            Icon(
                painter = painterResource(id = R.drawable.switch_circle),
                contentDescription = if(switchOn) "Enabled" else "Disabled",
                modifier = Modifier
                    .size(thumbSize)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .padding(all = iConInnerPadding),
            )
        }
    }
}

fun showMessage(context: Context, message:String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetValue = targetBiasValue)
    return remember { derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) } }
}


@Composable
fun WeeklyChecklistApp(main: MainActivity){
    WeeklyCheckListTheme {
        Surface(
            modifier = Modifier,
        ) {
            CustomToggleButton(isCheck = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
}

@Preview(showBackground = false)
@Composable
fun SwitchPreview(){
    Column{
        CustomToggleButton(isCheck = true)
        CustomToggleButton(isCheck = false)
    }
}
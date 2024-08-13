package com.weekly.weeklychecklist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.weekly.weeklychecklist.ui.theme.SuperLightGray
import kotlinx.coroutines.delay

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
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

@Composable
fun CustomSpacer(height: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
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
fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetValue = targetBiasValue)
    return remember { derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) } }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }
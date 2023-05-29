package com.weekly.weeklychecklist

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.ui.ChecklistSwipable
import com.weekly.weeklychecklist.ui.ChecklistWriteBoard
import com.weekly.weeklychecklist.ui.theme.Red2
import com.weekly.weeklychecklist.ui.theme.Red4
import com.weekly.weeklychecklist.ui.theme.WeeklyCheckListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyChecklistApp(this)
        }
    }
}

//앱 전체 컴포저블
@Composable
fun WeeklyChecklistApp(main: MainActivity) {
    val cornerSize = 7
    val boxHeight = LocalConfiguration.current.screenHeightDp.minus(100).dp
    
    WeeklyCheckListTheme {
        //뒷 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Red4,
                    shape = RoundedCornerShape(cornerSize, cornerSize, 0, 0)
                ),
            contentAlignment = Alignment.BottomCenter
        )
        {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                //타이틀
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "나만의 계획표",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                //격자
                Spacer( modifier = Modifier.size(1.dp).weight(1f) )
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
                    ListTodo()
                }
            }
            FloatingActions(main)
        }
    }
}
//투두 리스트
@Composable
fun ListTodo() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ChecklistSwipable()
        
    }
}

//플로팅 버튼
@Composable
fun FloatingActions(context: Context) {
    var isPressed by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 10.dp, end = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        FloatingActionButton(
            modifier = Modifier.size(70.dp),
            backgroundColor = Red2,
            onClick = {
                isPressed = !isPressed
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                tint = Color.White,
                contentDescription = "Add"
            )
        }
    }
    if (isPressed)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .onFocusEvent {
                    //TODO 포커스 변경 시 WriteBoard 가 보이지 않도록 조치해야함
                    when(it.isFocused){
                        true -> {
                            Toast.makeText(context, "isFocused = true", Toast.LENGTH_SHORT).show()
                        }
                        false -> {
                            Toast.makeText(context, "isFocused = false", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        ) {
            ChecklistWriteBoard()
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {
    WeeklyChecklistApp(MainActivity())
}
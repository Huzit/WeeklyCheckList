package com.weekly.weeklychecklist

import android.os.Bundle
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
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.ui.AddCheckListButton
import com.weekly.weeklychecklist.ui.ChecklistSwipable
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
    val cornerSize = 10
    WeeklyCheckListTheme {
        Surface(
            modifier = Modifier,
            color = Red4,
            shape = RoundedCornerShape(cornerSize, cornerSize, 0, 0),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "나만의 계획표",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Spacer(
                    modifier = Modifier
                        .size(1.dp)
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(cornerSize, cornerSize, 0, 0))
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChecklistSwipable()
                        Spacer(
                            modifier = Modifier
                                .size(1.dp)
                                .weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(bottom = 10.dp),
                            contentAlignment = Alignment.TopEnd
                        ){
                            AddCheckListButton {

                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting(){
    WeeklyChecklistApp(MainActivity())
}
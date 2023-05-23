package com.weekly.weeklychecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weekly.weeklychecklist.ui.AddCheckListButton
import com.weekly.weeklychecklist.ui.ChecklistBox
import com.weekly.weeklychecklist.ui.ChecklistSwipable
import com.weekly.weeklychecklist.ui.ChecklistWriteBoard
import com.weekly.weeklychecklist.ui.CustomToggleButton
import com.weekly.weeklychecklist.ui.NotificationDialog
import com.weekly.weeklychecklist.ui.WeekSelectButton
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
    WeeklyCheckListTheme {
        Surface(
            modifier = Modifier,
        ) {
            Column {
                Row {
                    ChecklistBox(text = "가가가가가가가가가가가가가가가가가가가가가가가가가가가가")
                    CustomToggleButton(isCheck = false)
                }
                ChecklistSwipable()
                ChecklistWriteBoard()
            }
        }
    }
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
        NotificationDialog(message = "초기화 하시겠습니까?", positiveEvent = { /*TODO*/ }, negativeEvent = { /*TODO*/})
    }
}
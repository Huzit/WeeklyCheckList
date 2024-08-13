package com.weekly.weeklychecklist.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.MyDayOfWeek
import com.weekly.weeklychecklist.ui.theme.ClickedYellow

//요일 선택 버튼
@Composable
fun weekSelectButton(
    week: MyDayOfWeek,
    size: Dp = 35.dp,
    fontSize: TextUnit = 13.sp,
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
    if (isContain) {
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
                backgroundColor = if (isClicked) {
                    //클릭 시 추가
                    returnSet.add(week)
                    ClickedYellow
                } else {
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
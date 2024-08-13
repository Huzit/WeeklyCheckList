package com.weekly.weeklychecklist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weekly.weeklychecklist.ui.theme.ConfirmButton

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

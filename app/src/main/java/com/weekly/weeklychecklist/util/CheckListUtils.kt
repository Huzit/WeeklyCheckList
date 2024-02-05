package com.weekly.weeklychecklist.util

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import com.weekly.weeklychecklist.MyDayOfWeek
import java.time.DayOfWeek

class CheckListUtils {
    //java.time.DayOfWeek 를 MyDayOfWeek로 변경
    fun convertDayOfWeekToMyDayOfWeek(weeks: DayOfWeek) = when (weeks) {
        DayOfWeek.MONDAY -> MyDayOfWeek.월
        DayOfWeek.TUESDAY -> MyDayOfWeek.화
        DayOfWeek.WEDNESDAY -> MyDayOfWeek.수
        DayOfWeek.THURSDAY -> MyDayOfWeek.목
        DayOfWeek.FRIDAY -> MyDayOfWeek.금
        DayOfWeek.SATURDAY -> MyDayOfWeek.토
        DayOfWeek.SUNDAY -> MyDayOfWeek.일
    }

    fun convertMyDayOfWeekToDayOfWeek(weeks: MyDayOfWeek) = when (weeks) {
        MyDayOfWeek.월 -> DayOfWeek.MONDAY
        MyDayOfWeek.화 -> DayOfWeek.TUESDAY
        MyDayOfWeek.수 -> DayOfWeek.WEDNESDAY
        MyDayOfWeek.목 -> DayOfWeek.THURSDAY
        MyDayOfWeek.금 -> DayOfWeek.FRIDAY
        MyDayOfWeek.토 -> DayOfWeek.SATURDAY
        MyDayOfWeek.일 -> DayOfWeek.SUNDAY
        else -> ""
    }

    object CustomIndication: Indication{
        private class DefaultDebugIndicationInstance(private val isPressed: State<Boolean>): IndicationInstance{
            override fun ContentDrawScope.drawIndication(){
                drawContent()
                if (isPressed.value){
                    drawRect(color = Color.Gray.copy(alpha = 0.1f), size = size)
                }
            }
        }
        @Composable
        override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
            val isPressed = interactionSource.collectIsPressedAsState()
            return remember(interactionSource){
                DefaultDebugIndicationInstance(isPressed)
            }
        }
    }
}
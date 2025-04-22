/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostwalker18.schedule.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.Orientation
import com.ghostwalker18.schedule.getScreenOrientation
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.ui.theme.ScheduleTableFontSize
import com.ghostwalker18.schedule.ui.theme.gray500Color
import com.ghostwalker18.schedule.utils.Utils
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import java.util.*

/**
 * Эта функция отображает таблицу с расписанием
 * @param lessons занятия на день для отображения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ScheduleTable(lessons: Array<Lesson>){
    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Max)
    ) {
        if(lessons.isNotEmpty() && Utils.isDateToday(lessons[0].date))
            Icon(Icons.Outlined.AccessTime, null, Modifier.alpha(0f))
        TableCell(stringResource(Res.string.number), 0.1f, ScheduleTableFontSize)
        if(getScreenOrientation() == Orientation.LandScape)
            TableCell(stringResource(Res.string.times), 0.2f, ScheduleTableFontSize)
        TableCell(stringResource(Res.string.subject), 0.45f, ScheduleTableFontSize)
        TableCell(stringResource(Res.string.teacher), 0.2f, ScheduleTableFontSize)
        TableCell(stringResource(Res.string.room), 0.15f, ScheduleTableFontSize)
    }
    lessons.forEachIndexed{
            index, lesson ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max)
                .background(color = if (index % 2 == 0) gray500Color else MaterialTheme.colors.background)
        ) {
            if(Utils.isDateToday(lesson.date))
                when(Utils.isLessonAvailable(lesson.date, lesson.times)){
                    Utils.LessonAvailability.ENDED -> Icon(
                        imageVector = Icons.Outlined.EventBusy,
                        contentDescription = stringResource(Res.string.lesson_ended_descr)
                    )
                    Utils.LessonAvailability.STARTED -> Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = stringResource(Res.string.lesson_available_descr)
                    )
                    Utils.LessonAvailability.NOT_STARTED -> Icon(
                        imageVector = Icons.Outlined.EventAvailable,
                        contentDescription = stringResource(Res.string.lesson_not_started_descr)
                    )
                    null -> return
                }
            TableCell(lesson.number, 0.1f, ScheduleTableFontSize)
            if(getScreenOrientation() == Orientation.LandScape)
                TableCell(lesson.times?: "", 0.2f, ScheduleTableFontSize)
            TableCell(lesson.subject, 0.45f, ScheduleTableFontSize)
            TableCell(lesson.teacher?: "", 0.2f, ScheduleTableFontSize)
            TableCell(lesson.room?: "", 0.15f, ScheduleTableFontSize)
        }
    }
}

/**
 * Эта функция отображает клетку в расписании
 * @param text содержимое клетки
 * @param weight ширина клетки в процентах от таблицы
 *
 * @author Ipatov Nikita
 * @since 1.0
 */
@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
    fontSize: TextUnit = TextUnit.Unspecified
){
    Column(
        modifier = Modifier
            .weight(weight)
            .padding(8.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = text,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Этот метод генерирует заголовок для этого элемента
 * @param date дата расписания
 * @param dayOfWeekId id строкового ресурса соответствующего дня недели
 * @return заголовок
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun Title(
    date: Calendar,
    dayOfWeekId: StringResource,
    modifier: Modifier = Modifier
){
    val dayOfWeek: String = stringResource(dayOfWeekId)
    var label = dayOfWeek + " (" + Utils.generateDateForTitle(date) + ")"
    if (Utils.isDateToday(date)) {
        label = label + " - " + stringResource(Res.string.today)
    }
    Text(label, modifier)
}
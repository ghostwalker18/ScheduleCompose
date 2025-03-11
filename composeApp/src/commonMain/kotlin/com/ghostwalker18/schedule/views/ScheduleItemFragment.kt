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

package com.ghostwalker18.schedule.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.getNavigator
import com.ghostwalker18.schedule.getNotesRepository
import com.ghostwalker18.schedule.ui.theme.gray500Color
import kotlinx.coroutines.launch
import com.ghostwalker18.schedule.models.Lesson
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import com.ghostwalker18.schedule.utils.Utils
import com.ghostwalker18.schedule.viewmodels.DayModel
import com.ghostwalker18.schedule.viewmodels.ScheduleModel
import java.util.*

/**
 * Эта функция отображает элемент расписания на день
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ScheduleItemFragment(dayOfWeek: StringResource) {
    val navigator = getNavigator()
    val scheduleModel = viewModel{ ScheduleModel() }
    val notesRepository = getNotesRepository()
    val weekDayNumbers = hashMapOf(
        Res.string.monday to Calendar.MONDAY,
        Res.string.tuesday to Calendar.TUESDAY,
        Res.string.wednesday to Calendar.WEDNESDAY,
        Res.string.thursday to Calendar.THURSDAY,
        Res.string.friday to Calendar.FRIDAY
    )
    val model = viewModel(key =dayOfWeek.key) { DayModel() }
    val date by model.getDate().collectAsState()
    val lessons by model.lessons.collectAsState()
    val isOpened by model.isOpened.collectAsState()
    val notesCount by notesRepository.getNotesCount(model.group?: "", date).collectAsState(0)

    model.viewModelScope.launch {
        scheduleModel.group.collect{
            model.group = it
        }
    }
    model.viewModelScope.launch {
        scheduleModel.teacher.collect{
            model.teacher = it
        }
    }
    model.viewModelScope.launch {
        scheduleModel.calendar.collect{
            model.setDate(Calendar.Builder()
                .setWeekDate(
                    scheduleModel.getYear(),
                    scheduleModel.getWeek(),
                    weekDayNumbers[dayOfWeek]!!
                )
                .build()
            )
        }
    }

    LaunchedEffect(key1 = date){
        model.isOpened.value = Utils.isDateToday(date)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { model.isOpened.value = !isOpened }
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ){
                Title(date, dayOfWeek, Modifier.align(Alignment.Center))
                val modifier = Modifier.align(Alignment.CenterEnd)
                if(isOpened){
                    Icon(Icons.Filled.KeyboardArrowUp, "", modifier)
                }
                else{
                    Icon(Icons.Filled.KeyboardArrowDown, "", modifier)
                }
            }
        }
        AnimatedVisibility(isOpened){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScheduleTable(lessons)
                IconButton(
                    { navigator.goNotesActivity(model.group!!, model.getDate().value) }
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(Icons.AutoMirrored.Filled.Notes, "")
                        if(notesCount > 0)
                            Text(text = notesCount.toString())
                    }
                }
            }
        }
    }
}

/**
 * Эта функция отображает таблицу с расписанием
 * @param lessons занятия на день для отображения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
private fun ScheduleTable(lessons: Array<Lesson>){
    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Max)
    ) {
        if(lessons.isNotEmpty() && Utils.isDateToday(lessons[0].date))
            Icon(Icons.Outlined.AccessTime, null, Modifier.alpha(0f))
        TableCell(stringResource(Res.string.number), 0.1f)
        TableCell(stringResource(Res.string.times), 0.2f)
        TableCell(stringResource(Res.string.subject), 0.45f)
        TableCell(stringResource(Res.string.teacher), 0.2f)
        TableCell(stringResource(Res.string.room), 0.15f)
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
                    Utils.LessonAvailability.ENDED -> Icon(Icons.Outlined.EventBusy, null)
                    Utils.LessonAvailability.STARTED -> Icon(Icons.Outlined.AccessTime, null)
                    Utils.LessonAvailability.NOT_STARTED -> Icon(Icons.Outlined.EventAvailable, null)
                    null -> return
                }
            TableCell(lesson.number, 0.1f)
            TableCell(lesson.times?: "", 0.2f)
            TableCell(lesson.subject, 0.45f)
            TableCell(lesson.teacher?: "", 0.2f)
            TableCell(lesson.room?: "", 0.15f)
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
private fun RowScope.TableCell(text: String, weight: Float ) {
    Column(
        modifier = Modifier
        .weight(weight)
        .padding(8.dp)
        .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = text,
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
private fun Title(date: Calendar, dayOfWeekId: StringResource, modifier: Modifier = Modifier){
    val dayOfWeek: String = stringResource(dayOfWeekId)
    var label = dayOfWeek + " (" + Utils.generateDateForTitle(date) + ")"
    if (Utils.isDateToday(date)) {
        label = label + " - " + stringResource(Res.string.today)
    }
    Text(label, modifier)
}
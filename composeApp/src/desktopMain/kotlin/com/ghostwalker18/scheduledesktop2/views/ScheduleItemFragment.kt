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

package com.ghostwalker18.scheduledesktop2.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import getNavigator
import kotlinx.coroutines.launch
import models.Lesson
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import utils.Utils
import viewmodels.DayModel
import viewmodels.ScheduleModel
import java.util.*

@Composable
fun ScheduleItemFragment(dayOfWeek: StringResource) {
    val navigator = getNavigator()
    val scheduleModel = viewModel{ScheduleModel()}
    val weekDayNumbers = hashMapOf(
        Res.string.monday to Calendar.MONDAY,
        Res.string.tuesday to Calendar.TUESDAY,
        Res.string.wednesday to Calendar.WEDNESDAY,
        Res.string.thursday to Calendar.THURSDAY,
        Res.string.friday to Calendar.FRIDAY
    )
    var isOpened by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(Calendar.getInstance()) }
    var lessons by remember { mutableStateOf(emptyArray<Lesson>()) }
    val model = viewModel(key = stringResource(dayOfWeek)) { DayModel() }

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
    model.viewModelScope.launch {
        model.lessons.collect{
            lessons = it
        }
    }

    model.viewModelScope.launch {
        model.getDate().collect{
            date = it
        }
    }

    /*LaunchedEffect(key1 = null){
        model.getDate().collect{
            date = it
        }
    }*/
    LaunchedEffect(key1 = date){
        isOpened = Utils.isDateToday(date)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { isOpened = !isOpened }
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
                    { navigator.goNotesActivity() }
                ){
                    Icon(Icons.AutoMirrored.Filled.Notes, "")
                }
            }
        }
    }
}

@Composable
fun ScheduleTable(lessons: Array<Lesson>){
    Row {
        TableCell(stringResource(Res.string.number), 0.1f)
        TableCell(stringResource(Res.string.times), 0.2f)
        TableCell(stringResource(Res.string.subject), 0.45f)
        TableCell(stringResource(Res.string.teacher), 0.2f)
        TableCell(stringResource(Res.string.room), 0.15f)
    }
    for (lesson in lessons){
        Row {
            TableCell(lesson.lessonNumber, 0.1f)
            TableCell(lesson.times?: "", 0.2f)
            TableCell(lesson.subject, 0.45f)
            TableCell(lesson.teacher?: "", 0.2f)
            TableCell(lesson.roomNumber?: "", 0.15f)
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(IntrinsicSize.Max)
    )
}

/**
 * Этот метод генерирует заголовок для этого элемента
 * @param date дата расписания
 * @param dayOfWeekId id строкового ресурса соответствующего дня недели
 * @return заголовок
 */
@Composable
fun Title(date: Calendar, dayOfWeekId: StringResource, modifier: Modifier = Modifier){
    val dayOfWeek: String = stringResource(dayOfWeekId)
    var label = dayOfWeek + " (" + Utils.generateDateForTitle(date) + ")"
    if (Utils.isDateToday(date)) {
        label = label + " - " + stringResource(Res.string.today)
    }
    Text(label, modifier)
}
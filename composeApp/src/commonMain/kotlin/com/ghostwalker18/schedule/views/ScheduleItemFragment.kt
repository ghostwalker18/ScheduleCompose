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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.ScheduleApp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import com.ghostwalker18.schedule.utils.Utils
import com.ghostwalker18.schedule.viewmodels.DayModel
import com.ghostwalker18.schedule.viewmodels.ScheduleModel
import com.russhwolf.settings.get
import java.util.*
import com.ghostwalker18.schedule.widgets.*

/**
 * Эта функция отображает элемент расписания на день
 * @param date дата для отображения расписания
 * @param dayOfWeek названия дня недели
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ScheduleItemFragment(
    date: Calendar,
    dayOfWeek: StringResource
) {
    val navigator = ScheduleApp.instance.getNavigator()
    val scheduleModel = viewModel{ ScheduleModel(date) }
    val notesRepository = ScheduleApp.instance.notesRepository
    val weekDayNumbers = hashMapOf(
        Res.string.monday to Calendar.MONDAY,
        Res.string.tuesday to Calendar.TUESDAY,
        Res.string.wednesday to Calendar.WEDNESDAY,
        Res.string.thursday to Calendar.THURSDAY,
        Res.string.friday to Calendar.FRIDAY
    )

    val model = viewModel(key =dayOfWeek.key) { DayModel() }
    val mode = ScheduleApp.instance.preferences["scheduleStyle", "in_fragment"]
    if(mode == "in_activity") model.isOpened.value = false

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
        ContentWrapper(
            toolTip = Res.string.schedule_tooltip
        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    when(mode){
                        "in_fragment" -> model.isOpened.value = !isOpened
                        "in_activity" -> ScheduleApp.instance.getNavigator().goScheduleScreen(
                            model.getDate().value,
                            model.group,
                            model.teacher
                        )
                    }
                }
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    Title(date, dayOfWeek, Modifier.align(Alignment.Center))
                    val modifier = Modifier.align(Alignment.CenterEnd)
                    if(isOpened && mode == "in_fragment"){
                        Icon(Icons.Filled.KeyboardArrowUp, "", modifier)
                    }
                    else if( mode == "in_fragment"){
                        Icon(Icons.Filled.KeyboardArrowDown, "", modifier)
                    }
                }
            }
        }
        AnimatedVisibility(isOpened){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScheduleTable(lessons)
                ContentWrapper(
                    toolTip = Res.string.notes_tooltip
                ){
                    IconButton(
                        {
                            model.group?.let {
                                navigator.goNotesActivity(
                                    group = it, date = model.getDate().value
                                )
                            }
                        }
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = stringResource(Res.string.notes_tooltip)
                            )
                            if(notesCount > 0)
                                Text(text = notesCount.toString())
                        }
                    }
                }
            }
        }
    }
}